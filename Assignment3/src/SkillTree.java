import lib280.list.LinkedIterator280;
import lib280.list.LinkedList280;
import lib280.tree.BasicMAryTree280;
import lib280.tree.MAryNode280;

public class SkillTree extends BasicMAryTree280<Skill> {

	/**	
	 * Create lib280.tree with the specified root node and
	 * specified maximum arity of nodes.  
	 * @timing O(1) 
	 * @param x item to set as the root node
	 * @param m number of children allowed for future nodes 
	 */
	public SkillTree(Skill x, int m)
	{
		super(x,m);
	}

	/**
	 * A convenience method that avoids typecasts.
	 * Obtains a subtree of the root.
	 * 
	 * @param i Index of the desired subtree of the root.
	 * @return the i-th subtree of the root.
	 */
	public SkillTree rootSubTree(int i) {
		return (SkillTree)super.rootSubtree(i);
	}

	/**
	 * Obtains all the skill prerequisites for obtaining an input skill (including the input skill) and returns
	 * them in list form
	 *
	 * @precond Target skill must be in skill tree
	 * @param skill_name Name of the target skill
	 * @return a list of all skill dependencies required to attain the target skill IF the skill is present. Root of the skill tree will be first. Includes the target skill at the last item in the list
	 * @throws RuntimeException If the skill is not in the skill tree
	 */
	public LinkedList280<Skill> skillDependencies(String skill_name) throws RuntimeException{
		LinkedList280<Skill> result = skillDependenciesTraversal(skill_name);
		if (result.isEmpty()){
			throw new RuntimeException( skill_name + ", is not in the skill tree");
		}
		return result;
	}

	/**
	 * Recursive traversal of the skill tree and if a target skill is found, as the recursion unwinds, add the skill dependencies to a list
	 *
	 * @param skill_name Target skill to find
	 * @return A list of all target skill dependencies
	 */
	private LinkedList280<Skill> skillDependenciesTraversal(String skill_name) {
		LinkedList280<Skill> result = new LinkedList280<Skill>();

		if (skill_name.equals(this.rootItem().skillName)){
			result.insertFirst(this.rootItem());
			return result;
		}

		for (int i = 1; i <= rootNode.lastNonEmptyChild(); i++) {
			result = rootSubTree(i).skillDependenciesTraversal(skill_name);
			if (!result.isEmpty()){
				result.insertFirst(rootItem());
				break;
			}
		}

		return result;
	}

	/**
	 * Returns the amount of points needed to invest to attain a target skill
	 *
	 * @precond Skill tree must have the target skill
	 * @param skill_name Target skill
	 * @return The total amount of points needed to invest to attain the target skill
	 * @throws RuntimeException if the target skill is not in the skill tree
	 */
	public int skillTotalCost(String skill_name) throws RuntimeException{
		LinkedList280<Skill> path = skillDependencies(skill_name);
		int cost = 0;
		LinkedIterator280<Skill> iterator = path.iterator();
		while (iterator.itemExists()){
			cost += iterator.item().skillCost;
			iterator.goForth();
		}
		return cost;
	}

	public static void main(String[] args) {
		Skill rootSkill = new Skill("Militia Beginner","Root for all of the combat skills. +5 atk. +5 def",1);
		SkillTree st = new SkillTree(rootSkill, 2);
		BasicMAryTree280<Skill> offense = new SkillTree(new Skill("Offensive Path","Hone your aggressive abilities. +10 atk",1),5);
		BasicMAryTree280<Skill> defense = new SkillTree(new Skill("Defensive Path","Temper your determination. +10 def",1),3);
		st.setRootSubtree(offense,1);
		st.setRootSubtree(defense,2);
		BasicMAryTree280<Skill> critcalTree = new SkillTree(new Skill("Critical Expert","Find the weak spots. +10 crit chance",2),4);
		BasicMAryTree280<Skill> dualWieldTree = new SkillTree(new Skill("Dual Wielding","Find the weak spots. +10 hit chance on offhand",3),3);
		BasicMAryTree280<Skill> twoHandedTree = new SkillTree(new Skill("Giant Weapon Wielding","You can use giant weapons. You won't need any extra bonus.",3),3);
		BasicMAryTree280<Skill> evasionTree = new SkillTree(new Skill("Evasion Expert","Nimble and spry. +20 dodge chance",3),4);
		BasicMAryTree280<Skill> armorTree = new SkillTree(new Skill("Armored Advocate","You have no weak spots!. +15 def and halved armor penalties",2),3);
		BasicMAryTree280<Skill> shieldTree = new SkillTree(new Skill("Serious Shield Skills ","You provide cover for nearby allies!. +10 def nearby allies",2),3);
		offense.setRootSubtree(critcalTree,1);
		offense.setRootSubtree(dualWieldTree,2);
		offense.setRootSubtree(twoHandedTree,3);
		defense.setRootSubtree(evasionTree,1);
		defense.setRootSubtree(armorTree,2);
		defense.setRootSubtree(shieldTree,3);
		BasicMAryTree280<Skill> uncannyEvasion = new SkillTree(new Skill("Uncanny Evasion","Reduced AOE damage!",4),0);
		BasicMAryTree280<Skill> giantSlayer = new SkillTree(new Skill("Giant Slayer","Dodge and Attack bonus against giants, or giant weapon wielders",3),0);
		evasionTree.setRootSubtree(uncannyEvasion,1);
		evasionTree.setRootSubtree(giantSlayer,2);
		BasicMAryTree280<Skill> pinPointAnguish = new SkillTree(new Skill("Pin Point Anguish","+75% critical damage",4),0);
		BasicMAryTree280<Skill> bleedingSkill = new SkillTree(new Skill("Bleeding Gashes","Crits do bleed damage",4),0);
		critcalTree.setRootSubtree(pinPointAnguish,1);
		critcalTree.setRootSubtree(bleedingSkill,2);

		System.out.println(st.toStringByLevel());
		// Test skillDependencies() at root
		LinkedList280<Skill> skillSearch00 = st.skillDependencies("Militia Beginner");
		System.out.println("\nDependencies for Militia Beginner:\n" + skillSearch00.toString());
		// Test skillDependencies() at children
		LinkedList280<Skill> skillSearch01 = st.skillDependencies("Critical Expert");
		System.out.println("Dependencies for Critical Expert:\n" + skillSearch01.toString());
		LinkedList280<Skill> skillSearch02 = st.skillDependencies("Uncanny Evasion");
		System.out.println("Dependencies for Uncanny Evasion:\n" +skillSearch02.toString());
		// Test skillDependencies() at non existant skill
		System.out.println("Dependencies for Giantssssssssss Slayer:");
		try{
			LinkedList280<Skill> skillSearch03 = st.skillDependencies("Giantssssssssss Slayer");
			System.out.println("Error: Expected a thrown exception to occur");
		}
		catch (RuntimeException e){
			System.out.println("Giantssssssssss Slayer not found.");
			// Expected a thrown exception
		}

		// Test root skillTotalCost()
		if (st.skillTotalCost("Militia Beginner") != 1){
			System.out.println("Error: Militia Beginner path should cost 1, instead of: " + st.skillTotalCost("Militia Beginner"));
		}
		// Test root children  skillTotalCost()
		if (st.skillTotalCost("Critical Expert") != 4){
			System.out.println("Error: Critical expert path should cost 4, instead of: " + st.skillTotalCost("Critical Expert"));
		}
		if (st.skillTotalCost("Bleeding Gashes") != 8){
			System.out.println("Error: Bleeding Gashes path should cost 4, instead of: " + st.skillTotalCost("Bleeding Gashes"));
		}

		System.out.println("To get Militia Beginner you must invest " + st.skillTotalCost("Militia Beginner") + " points.");
		System.out.println("To get Critical Expert you must invest " + st.skillTotalCost("Critical Expert") + " points.");
		System.out.println("To get Bleeding Gashes you must invest " + st.skillTotalCost("Bleeding Gashes") + " points.");
		try{
			st.skillTotalCost("Fake Skill");
		}
		catch (RuntimeException e){
			System.out.println(e.getMessage());
		}
	}
	

}
