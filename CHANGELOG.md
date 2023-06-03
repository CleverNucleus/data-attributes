### Changelog

This is primarily a bug-fixing and optimisation update.

*Changed the way `/reload` works to refresh attributes: 

 - No longer saves the `updateFlag` to the level's nbt data.
 - No longer injects the `updateFlag` into vanilla packets.
 - Instead, we only use the `updateFlag` in runtime - not saving it at all, anywhere.

*Fixed [#80](https://github.com/CleverNucleus/data-attributes/issues/80): attribute tracking is handled differently now.

*Likely fixed an incompatibility between Data Attributes and ReplayMod: we no longer mess around with world properties at all.

**May* have fixed long-standing issues [24](https://github.com/CleverNucleus/data-attributes/issues/24) and [10](https://github.com/CleverNucleus/data-attributes/issues/10): almost all networking has been removed - now we only send/receive two custom packets in the whole mod: on game join and when `/reload` is executed. 

*Various performance improvements.