### Changelog

+Added hierarchy entity types that can be used to apply attributes to all entities that are an instance of an entity class. Currently supported types are as follows:

| **Identifier** | **Class Type** |
| -------------- | -------------- |
| `dataattributes:living_entity` | `LivingEntity` |
| `dataattributes:mob_entity` | `MobEntity` |
| `dataattributes:path_aware_entity` | `PathAwareEntity` |
| `dataattributes:hostile_entity` | `HostileEntity` |
| `dataattributes:passive_entity` | `PassiveEntity` |
| `dataattributes:animal_entity` | `AnimalEntity` |

These have a hierarchy of:

```
LivingEntity
  ┗ MobEntity
      ┗ PathAwareEntity
          ┣ HostileEntity
          ┗ PassiveEntity
              ┗ AnimalEntity
```

This feature is useful for when you want to modify the attributes of many different mobs, but do not know every mob's `EntityType` identifier.