package com.stolengalaxy.earthmc_meteor.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.stolengalaxy.earthmc_meteor.utils.Blacklist;
import meteordevelopment.meteorclient.commands.Command;
import net.minecraft.command.CommandSource;

public class BlacklistPlayerCommand extends Command {
    public BlacklistPlayerCommand(){
        super("blacklistplayer", "Blacklist a player", "blp");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder){
        builder.then(literal("add")
            .then(argument("username", StringArgumentType.string()).executes(context -> {
                String playerName = StringArgumentType.getString(context, "username");
                Blacklist.blacklistPlayer(playerName);

                return SINGLE_SUCCESS;
            }))
        );

    }
}
