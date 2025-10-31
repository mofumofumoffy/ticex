package moffy.ticex.block.proxy;

import moffy.ticex.block.proxy.entity.IProxyTicker;
import moffy.ticex.block.proxy.entity.ProxyBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nullable;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.POWERED;

public class ProxyBlock extends Block implements EntityBlock {
    public ProxyBlock(Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.stateDefinition.any().setValue(POWERED, false));
    }

    @Override
    public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, @Nullable LivingEntity pPlacer, ItemStack pStack) {
        super.setPlacedBy(pLevel, pPos, pState, pPlacer, pStack);
        BlockEntity te = pLevel.getBlockEntity(pPos);

        if(pPlacer != null && te instanceof ProxyBlockEntity proxyBlockEntity){
            proxyBlockEntity.setPlacerUUID(pPlacer.getUUID());
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> b) {
        b.add(POWERED);
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if(!pState.getValue(POWERED)){
            if (pPlayer.isShiftKeyDown()) {
                return InteractionResult.PASS;
            }

            BlockEntity te = pLevel.getBlockEntity(pPos);
            if(te != null){
                LazyOptional<IItemHandler> itemHandlerLazyOptional = te.getCapability(ForgeCapabilities.ITEM_HANDLER);
                if(itemHandlerLazyOptional.isPresent()){
                    IItemHandler itemHandler = itemHandlerLazyOptional.orElseThrow(IllegalStateException::new);
                    ItemStack internalStack = itemHandler.getStackInSlot(0);
                    if(pPlayer.getMainHandItem().isEmpty() && !internalStack.isEmpty()){
                        pPlayer.setItemSlot(EquipmentSlot.MAINHAND, internalStack.copy());
                        internalStack.shrink(1);
                        return InteractionResult.SUCCESS;
                    } else if(!pPlayer.getMainHandItem().isEmpty() && internalStack.isEmpty()) {
                        ItemStack copyStack = pPlayer.getMainHandItem().copy();
                        copyStack.setCount(1);
                        itemHandler.insertItem(0, copyStack, false);
                        pPlayer.getMainHandItem().shrink(1);
                        return InteractionResult.SUCCESS;
                    }
                }
            }
        }
        return super.use(pState, pLevel, pPos, pPlayer, pHand, pHit);
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        if (level.isClientSide) return;
        boolean powered = level.hasNeighborSignal(pos);
        if (powered != state.getValue(POWERED)) {
            level.setBlock(pos, state.setValue(POWERED, powered), Block.UPDATE_ALL);
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof ProxyBlockEntity pbe) pbe.onPowerChanged(powered);
        }
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ProxyBlockEntity(pos, state);
    }

    @Override
    public void onRemove(BlockState oldState, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!oldState.is(newState.getBlock())) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof ProxyBlockEntity pbe) {
                Containers.dropContents(level, pos, pbe.getDrops());
            }
            super.onRemove(oldState, level, pos, newState, isMoving);
        }
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        if(!pLevel.isClientSide()){
            return (lvl, pos, st, be) -> {
                if(st.getValue(POWERED) && be instanceof ProxyBlockEntity pbe && pbe.getProxiedCap().orElse(pbe.getMainItemHandler()) instanceof IProxyTicker proxyTicker){
                    proxyTicker.tick();
                }
            };
        }
        return null;
    }
}
