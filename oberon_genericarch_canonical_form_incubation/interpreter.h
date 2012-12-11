/*
 * interpreter.h
 *
 *  Created on: 09.12.2012
 *      Author: egp
 */

#ifndef INTERPRETER_H_
#define INTERPRETER_H_

#include "ModuleTable.h"
#include "wchar.h"

using namespace Oberon;

int run(ModuleTable* modules, wchar_t* fileName);



#endif /* INTERPRETER_H_ */
