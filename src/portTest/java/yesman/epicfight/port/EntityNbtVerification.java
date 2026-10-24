package yesman.epicfight.port;

import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import yesman.epicfight.platform.fabric.resource.RootNbtStorage;

public final class EntityNbtVerification {
    public static void main(String[] args) {
        var output=TagValueOutput.createWithoutContext(ProblemReporter.DISCARDING);
        output.putString("id","minecraft:player");
        output.putInt("Health",20);
        var data=new CompoundTag();
        var skills=new CompoundTag();
        skills.putString("skill","epicfight:roll");
        skills.putIntArray("slots",new int[]{0,3,7});
        data.put("epicfight",skills);
        data.putFloat("stamina",12.5F);
        RootNbtStorage.write(output,data);
        var stored=output.buildResult();
        if (!stored.getStringOr("id","").equals("minecraft:player") || stored.getIntOr("Health",0)!=20) {
            throw new AssertionError("Mod data overwrote vanilla entity fields");
        }
        var input=TagValueInput.create(ProblemReporter.DISCARDING,RegistryAccess.EMPTY,stored);
        var restored=RootNbtStorage.read(input);
        if (!restored.getCompoundOrEmpty("epicfight").equals(skills) || restored.getFloatOr("stamina",0)!=12.5F) {
            throw new AssertionError("Root-level skill/stamina data did not survive typed storage");
        }
        if (!restored.equals(stored)) throw new AssertionError("Root NBT schema changed on roundtrip");
        System.out.println("PORT_ENTITY_NBT_VERIFICATION_PASSED");
    }
}
