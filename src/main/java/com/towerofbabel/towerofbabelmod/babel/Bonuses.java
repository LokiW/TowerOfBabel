package com.towerofbabel.towerofbabelmod.babel;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import java.util.UUID;
import java.util.Map;
import java.util.HashMap;
import java.io.Serializable;


public class Bonuses implements Serializable {
	private static long key1 = 853L;
	private static long key2 = 9274L;

	public static enum OPERATOR {
		BASE(-1), ADD(0), MULTIPLIER(1), MULTIPLY(2);
		private int value;
		private OPERATOR(int v) {
			this.value = v;
		}
		public int getValue() {
			return this.value;	
		}
	}
	public static enum BONUS {
		/*
		 * generic.attackDamage
		 * generic.attackSpeed
		 * generic.luck
		 * generic.reachDistance
		 * generic.maxHealth
		 * generic.knockbackResistance
		 * generic.movementSpeed
		 * generic.armor
		 * generic.armorToughness
		 * forge.swimSpeed
		 */
		ATTACK_DAMAGE("generic.attackDamage"), ATTACK_SPEED("generic.attackSpeed"),
		LUCK("generic.luck"), REACH("generic.reachDistance"),
		MAX_HEALTH("generic.maxHealth"), KNOCKBACK_RESISTANCE("generic.knockbackResistance"),
		MOVE_SPEED("generic.movementSpeed"), SWIM_SPEED("forge.swimSpeed"),
		ARMOR("generic.armor"), ARMOR_TOUGHNESS("generic.armorToughness");

		private String value;
		private BONUS(String v) {
			this.value = v;
		}
		public String getValue() {
			return this.value;	
		}
	};

	public static class BonusTracker {
		public Map<OPERATOR, Double> values;
		public Map<OPERATOR, UUID> ids;

		public BonusTracker(BonusTracker other) {
			this.values = other.values;
			this.ids = other.ids;
		}

		public BonusTracker() {
			this.values = new HashMap<OPERATOR, Double>();
			this.ids = new HashMap<OPERATOR, UUID>();
		}

		public void combine(BonusTracker other) {
			for (OPERATOR op : other.values.keySet()) {
				if (values.get(op) != null) {
					values.put(op, resolveBonus(op, values.get(op), other.values.get(op)));
				} else {
					values.put(op, other.values.get(op));
				}
				if (ids.get(op) == null) {
					ids.put(op, other.ids.get(op));
				}
			}	
		}

		public void applyAll(EntityPlayer p, BONUS b) {
			for (OPERATOR o : values.keySet()) {
				ids.put(o, Bonuses.applyBonus(p, ids.get(o), b, o, values.get(o)));
			}
		}

		private Double resolveBonus(OPERATOR operator, Double value1, Double value2) {
			// TODO switch over Bonuses
			switch(operator) {
				case BASE:
					return Double.max(value1, value2);	
				case ADD:
				case MULTIPLIER:
					return value1 + value2;
				case MULTIPLY:
					return value1 * value2;
				default:
					System.out.println("TowerOfBabel: ERROR: invalid numerical bonus operation "+operator);
					return value1;
			}
		}

	}

	public static UUID applyBonus(EntityPlayer p, UUID attributeId, BONUS bonus, OPERATOR operator, Double value) {
		AbstractAttributeMap playerAttributes = p.getAttributeMap();
		IAttributeInstance toChange = playerAttributes.getAttributeInstanceByName(bonus.getValue());
		if (toChange == null) {
			// TODO instantiate attributes that are not available by default
			System.out.println("TowerOfBabel: Player does not have attribute "+bonus);
			return null;
		}

		switch(operator) {
			case BASE:
				toChange.setBaseValue(value);
				return null;
			case ADD:
			case MULTIPLIER:
			case MULTIPLY:
				return applyAttr(p, toChange, attributeId, bonus, value, operator.getValue());
			default:
				return null;
		}
	}



	/*
	 * Apply an attribute modifier to an entity
	 * modifierType: 0 is add, 1 is multiply, 2 is multiply (affecting other multipliers)
	 * key needs to be unique to the entity, attr pair
	 */
	private static UUID applyAttr(EntityPlayer p, IAttributeInstance attr, UUID oldId, BONUS b, double modifierValue, int modifierType) {
		if (attr == null) {
			System.out.println("TowerOfBabel: Couldn't update attribute "+b.toString());
			return oldId;
		}
		
		UUID id = genId(p, oldId, b);
		if (attr.getModifier(id) != null) {
			attr.removeModifier(id);		
		}

		AttributeModifier modifier = new AttributeModifier(id, "TOB Bonus "+b.toString(), modifierValue, modifierType);
		attr.applyModifier(modifier);
		return id;
	}

	private static UUID genId(EntityPlayer p, UUID oldId, BONUS b) {
		if (oldId == null) {
			return new UUID(key1+b.ordinal(), key2+p.getUniqueID().getLeastSignificantBits());
		}	
		return oldId;
	}

}
