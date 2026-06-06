package moffy.ticex.modifier.propeties;

import com.brandon3055.brandonscore.api.power.IOPStorage;
import com.brandon3055.brandonscore.api.power.OPStorage;
import com.brandon3055.draconicevolution.api.capability.DECapabilities;
import com.brandon3055.draconicevolution.api.capability.PropertyProvider;
import com.brandon3055.draconicevolution.api.config.*;
import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.ILuaFunction;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.MethodResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.LogicalSidedProvider;
import net.minecraftforge.fml.LogicalSide;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;

public class EvolvedProperty {
    public static BiFunction<Player, ItemStack, Map<String, Object>> getProperties() {
        return (user, stack) -> {
            Map<String, Object> result = new HashMap<>();

            result.put("getOPAmount", getOPAmount(user, stack));
            result.put("getModuleData", getModuleData(user, stack));
            result.put("setModuleData", setModuleData(user, stack));

            return result;
        };
    }
    public static ILuaFunction getOPAmount(Player user, ItemStack stack){
        return args -> {
            var opStorageLazyOptional = stack.getCapability(DECapabilities.OP_STORAGE);
            if(opStorageLazyOptional.isPresent()){
                IOPStorage opStorage = opStorageLazyOptional.orElseThrow(IllegalStateException::new);
                return MethodResult.of(opStorage.getOPStored(), opStorage.getMaxOPStored());
            }
            return MethodResult.of();
        };
    }

    public static ILuaFunction getModuleData(Player user, ItemStack stack){
        return args -> {
            Map<String, Object> result = new HashMap<>();
            stack.getCapability(DECapabilities.PROPERTY_PROVIDER_CAPABILITY).ifPresent(propertyProvider -> {
                for(var property : propertyProvider.getProperties()){
                    String name = property.getName();
                    result.put(name, getValueForName(propertyProvider, name));
                }
            });
            return MethodResult.of(result);
        };
    }

    public static ILuaFunction setModuleData(Player user, ItemStack stack){
        return args -> {
            String propertyName = args.getString(0);
            var propertyProviderLazyOptional = stack.getCapability(DECapabilities.PROPERTY_PROVIDER_CAPABILITY);
            if(propertyProviderLazyOptional.isPresent()){
                PropertyProvider provider = propertyProviderLazyOptional.orElseThrow(IllegalStateException::new);
                return setValueForName(provider, propertyName, args, stack);
            }
            return MethodResult.of(false);
        };
    }

    protected static Object getValueForName(PropertyProvider provider, String propertyName){
        return switch (Objects.requireNonNull(provider.getProperty(propertyName)).getType()){
            case BOOLEAN -> provider.getBool(propertyName).getValue();
            case INTEGER -> provider.getInt(propertyName).getValue();
            case DECIMAL -> provider.getDecimal(propertyName).getValue();
            case ENUM -> null; //Unused in equipment
        };
    }

    protected static MethodResult setValueForName(PropertyProvider provider, String propertyName, IArguments args, ItemStack stack){
        try{
            if(provider.hasProperty(propertyName)){
                switch (Objects.requireNonNull(provider.getProperty(propertyName)).getType()){
                    case BOOLEAN:
                        boolean newBooleanValue = args.getBoolean(1);
                        LogicalSidedProvider.WORKQUEUE.get(LogicalSide.SERVER).execute(() -> {
                            BooleanProperty booleanProperty = provider.getBool(propertyName);
                            booleanProperty.setValue(newBooleanValue);
                            booleanProperty.onValueChanged(stack);
                        });
                        return MethodResult.of(true);
                    case INTEGER:
                        int newIntValue = args.getInt(1);
                        LogicalSidedProvider.WORKQUEUE.get(LogicalSide.SERVER).execute(() -> {
                            IntegerProperty integerProperty = provider.getInt(propertyName);
                            integerProperty.setValue(newIntValue);
                            integerProperty.onValueChanged(stack);
                        });
                        return MethodResult.of(true);
                    case DECIMAL:
                        double newDoubleValue = args.getDouble(1);
                        LogicalSidedProvider.WORKQUEUE.get(LogicalSide.SERVER).execute(() -> {
                            DecimalProperty decimalProperty = provider.getDecimal(propertyName);
                            decimalProperty.setValue(newDoubleValue);
                            decimalProperty.onValueChanged(stack);
                        });
                        return MethodResult.of(true);
                    case ENUM:
                        break; //Unused in equipment
                }
            }
        } catch (LuaException e){
            return MethodResult.of(false, e.getMessage());
        }
        return MethodResult.of(false);
    }
}
