// micq.cpp : Defines the entry point for the console application.
//

#include "stdafxmicq.h"
#include "micqlib.h"

int main(int argc, char* argv[])
{
  initMicqlib();
  while(!quitMicqlib())
    tickMicqlib();
  deinitMicqlib();
  return 0;
}

