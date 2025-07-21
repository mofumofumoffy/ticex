package moffy.ticex.client.rendering;

import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.modifiers.ModifierId;

import java.util.function.Predicate;

public abstract class PartPredicate<T> {

    protected final Predicate<T> predicate;

    public PartPredicate(Predicate<T> predicate) {
        this.predicate = predicate;
    }

    public abstract boolean testPredicate(T value);

    public static class Material extends PartPredicate<MaterialVariantId> {

        public Material(Predicate<MaterialVariantId> predicate) {
            super(predicate);
        }

        public Material(MaterialVariantId variantId) {
            super(variantId::sameVariant);
        }

        @Override
        public boolean testPredicate(MaterialVariantId value) {
            return predicate.test(value);
        }
    }

    public static class Modifier extends PartPredicate<ModifierId> {

        public Modifier(Predicate<ModifierId> predicate) {
            super(predicate);
        }

        public Modifier(ModifierId modifierId) {
            super(modId -> modId.equals(modifierId));
        }

        @Override
        public boolean testPredicate(ModifierId value) {
            return predicate.test(value);
        }
    }
}
