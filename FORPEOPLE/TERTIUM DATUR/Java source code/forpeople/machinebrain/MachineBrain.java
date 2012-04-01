package forpeople.machinebrain;

import forpeople.events.ReadEvent;

public interface MachineBrain {
	/**
	 * <pre>
		(* eval should diagnose if a part of a program is formal, nonformal, or tertium datur *)
		(* co-goal with users *)
		(* "co-goal with users" --- это более точно означает, что пользователи --- это начальники 
		   для машины. У начальников есть свои междуначальнические (междупользовательские) отношения 
		   и обычаи и tertium datur. *)

		(* this is a formal eval which evaluates a formal program which reads and evaluates 
		   tertium datur programs from users *)
	 * </pre>
	 * @param dataReference A reference to data which was read
	 */
	void eval(ReadEvent dataReference);
}
