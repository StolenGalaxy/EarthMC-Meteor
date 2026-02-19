package com.stolengalaxy.earthmc_meteor.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.stolengalaxy.earthmc_meteor.utils.Blacklist;
import meteordevelopment.meteorclient.commands.Command;
import net.minecraft.command.CommandSource;

import java.util.List;

public class BlacklistNationCommand extends Command {
    public BlacklistNationCommand(){
        super("blacklistnation", "Blacklist a nation spawn", "bln");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder){
        builder.then(literal("add")
            .then(argument("nation", StringArgumentType.string()).executes(context -> {
                String nationName = StringArgumentType.getString(context, "nation").strip();

                List<String> currentList = Blacklist.getBlacklist("nation");

                if(currentList.contains(nationName)){
                    info(nationName + " is already a blacklisted nation spawn!");
                }else{
                    Blacklist.blacklist("nation", nationName);
                    info("Added " + nationName + " to nation spawn blacklist");
                }
                return SINGLE_SUCCESS;
            }))
        );

        builder.then(literal("remove")
            .then(argument("nation", StringArgumentType.string()).executes(context -> {
                String nationName = StringArgumentType.getString(context, "nation").strip();

                List<String> currentList = Blacklist.getBlacklist("nation");
                if(currentList.contains(nationName)){
                    Blacklist.unBlacklist("nation", nationName);
                    info("Removed " + nationName + " from nation spawn blacklist");
                } else{
                    info("Couldn't find " + nationName + " in nation spawn blacklist!");
                }

                return SINGLE_SUCCESS;
            }))
        );

        builder.then(literal("list").executes(context -> {
            List<String> blacklistedNations = Blacklist.getBlacklist("nation");

            int index = 1;
            for(String nationName:blacklistedNations){
                info("Blacklisted nation spawn #" + index + ": " + nationName);
                index++;
            }
            if(index == 1){
                info("Nation spawn blacklist is empty!");
            }
            return SINGLE_SUCCESS;
        }));

    }
}
