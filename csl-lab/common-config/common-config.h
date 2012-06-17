#ifndef __COMMON_CONF_H__
#define __COMMON_CONF_H__

#undef NETSTACK_CONF_RDC
#define NETSTACK_CONF_RDC     nullrdc_driver
/* #define NETSTACK_CONF_RDC     contikimac_driver */

#define CONTIKIMAC_CONF_MAX_PHASE_NEIGHBORS 7

#undef NULLRDC_CONF_802154_AUTOACK
#define NULLRDC_CONF_802154_AUTOACK 1

/* Reduce code size */
#undef ENERGEST_CONF_ON
#define ENERGEST_CONF_ON 0

#endif /* __COMMON_CONF_H__ */
