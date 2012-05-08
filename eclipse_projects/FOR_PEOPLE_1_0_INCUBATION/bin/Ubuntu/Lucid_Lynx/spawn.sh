ls --color=never -l $0
#curr.location===??? is: .=?/FOR_PEOPLE_1_0_INCUBATION/bin/Ubuntu/Lucid_Lynx/.
#..=/media/h1/ECLIPSE_WORKSPACE/FOR_PEOPLE_1_0_INCUBATION/bin/Ubuntu/
#../..=/media/h1/ECLIPSE_WORKSPACE/FOR_PEOPLE_1_0_INCUBATION/bin/
#../../..=/media/h1/ECLIPSE_WORKSPACE/FOR_PEOPLE_1_0_INCUBATION/
#../../../..=/media/h1/ECLIPSE_WORKSPACE/
FOR_PEOPLE=../../..
WORKSPACE=/media/h1/ECLIPSE_WORKSPACE
ws_oberon_interpreter_1_0_incubation=$WORKSPACE/oberon_interpreter_1_0_incubation

oi=./oberon_interpret
cd $ws_oberon_interpreter_1_0_incubation
pwd
[[ ! -x $oi ]] && make all
[[ -x $oi ]] && echo COMMAND_LINE: $WORKSPACE/ORDE_Oberon/SpawnORDE.Mod $*
[[ -x $oi ]] && $oi $WORKSPACE/ORDE_Oberon/SpawnORDE.Mod $*

