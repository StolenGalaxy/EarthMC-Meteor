package com.stolengalaxy.earthmc_meteor.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.stolengalaxy.earthmc_meteor.utils.Blacklist;
import meteordevelopment.meteorclient.commands.Command;
import net.minecraft.command.CommandSource;

import java.util.List;

public class BlacklistPlayerCommand extends Command {
    public BlacklistPlayerCommand(){
        super("blacklistplayer", "Blacklist a player", "blp");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder){
        builder.then(literal("add")
            .then(argument("username", StringArgumentType.string()).executes(context -> {
                String playerName = StringArgumentType.getString(context, "username").strip();

                List<String> currentList = Blacklist.getBlacklist("player");

                if(currentList.contains(playerName)){
                    info(playerName + " is already a blacklisted player!");
                }else{
                    Blacklist.blacklist("player", playerName);

                    info("Added " + playerName + " to player blacklist");
                }
                return SINGLE_SUCCESS;
            }))
        );

        builder.then(literal("remove")
            .then(argument("username", StringArgumentType.string()).executes(context -> {
                String playerName = StringArgumentType.getString(context, "username").strip();

                List<String> currentList = Blacklist.getBlacklist("player");
                if(currentList.contains(playerName)){
                    Blacklist.unBlacklist("player", playerName);
                    info("Removed " + playerName + " from player blacklist");
                } else{
                    info("Couldn't find " + playerName + " in player blacklist!");
                }

                return SINGLE_SUCCESS;
            }))
        );

        builder.then(literal("list").executes(context -> {
            List<String> blacklistedPlayers = Blacklist.getBlacklist("player");

            int index = 1;
            for(String playerName:blacklistedPlayers){
                info("Blacklisted player #" + index + ": " + playerName);
                index++;
            }
            if(index == 1){
                info("Player blacklist is empty!");
            }
            return SINGLE_SUCCESS;
        }));

    }
}
