package com;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.CommandException;
import net.minecraft.util.ChatComponentText;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import java.util.UUID;
import com.tower.SkillApplicator;

public class CLI extends CommandBase{

	@Override
	public String getCommandName() {
		return "tower";
	}

	private String USAGE = "[Player] add/remove skillname (default player is command sender)";
	@Override
	public void processCommand(ICommandSender ics, String[] args) throws CommandException {
		EntityPlayer p;
		String add;
		String skill;

		//align arguments
		if(args.length == 2 && ics instanceof EntityPlayerMP) {
			p = (EntityPlayerMP)ics;
			add = args[0];
			skill = args[1];
		} else if(args.length == 3) {
			p = CommandBase.getPlayer(ics,args[0]);
			add = args[1];
			skill = args[2];
		} else {
			throw new CommandException("Incorrect argument number: " + USAGE);
		}

		//error checks
		if(p == null) {
			throw new CommandException("Specified Player is not a Player");
		}
		if(!"add".equals(add) && !"remove".equals(add)) {
			throw new CommandException("got " + add + " instead of add or remove");
		}
		boolean addbool = "add".equals(add);
	
		SkillApplicator.learnSkill(p, addbool, skill);
	}

	@Override
	public String getCommandUsage(ICommandSender ics) {
		return "[player] add/remove skillname";
	}
}
