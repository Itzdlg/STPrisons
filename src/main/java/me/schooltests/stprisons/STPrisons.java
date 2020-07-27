package me.schooltests.stprisons;

import me.schooltests.stprisons.ranks.RankModule;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class STPrisons extends JavaPlugin {
    private BaseConfig<STPrisons> config;
    private RankModule rankModule;

    @Override
    public void onEnable() {
        config = new BaseConfig<STPrisons>(this) {
            @Override
            public void postLoad() { }
        };

        rankModule = new RankModule(this);
        if (config.getBooleanOrDefault("modules.rank")) rankModule.enable();
    }

    public RankModule getRankModule() {
        return rankModule;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage(ChatColor.RED + "This command is disabled!");
        return true;
    }
}