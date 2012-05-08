/* $Id: tabs.h,v 1.9 2004/07/03 18:08:35 kuhlmann Exp $ */

#ifndef MICQ_UTIL_TABS_H
#define MICQ_UTIL_TABS_H

void           TabInit (void);
void           TabAddIn  (const Contact *cont);
void           TabAddOut (const Contact *cont);
const Contact *TabGet    (int nr);
time_t         TabTime   (int nr);
int            TabHas    (const Contact *cont);

#endif /* MICQ_UTIL_TABS_H */
