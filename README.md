MyLineagePvpSystem
==================

Требования для плагина
----
- TagAPI http://dev.bukkit.org/bukkit-plugins/tag/files/34-v3-0-6/

Группы игроков
----
- Мирные игроки;
- Игроки, которые жаждят PVP;
- Игроки, которые убивают всех без разбора.


Конфиг
----
```
local:
  statusPeace: You went into a peaceful mode
  statusPVP: You went into a PVP mode
  statusPK: You have become a murderer, are imposed on you the effect of slowing, fatigue, weakness
  statisticPK: PK
  statisticPVP: PVP
  statisticKarma: Karma
  statisticDeaths: Deaths
  statisticDeathsMore: (from the other players)
  statisticModePeace: You are in a peace mode
  statisticModePVP: You are in a PVP mode
  statisticModePK: You are player killer
drop:
  inventory:
    peace: 0
    pvp: 10
    pk: 100
  armor:
    peace: 0
    pvp: 9
    pk: 99
experience:
  keep:
    peace: true
    pvp: false
    pk: false
karma:
  kill:
    peace: -10
    self: 10
    mob: 1
world:
  doesNotWork: creative,world_creative,world
  doNotCleanKarma: creative,world_creative
time:
  purple: 30
```

Внешние отличия
----
- Мирные игроки – с белым ником над головой;
- PVP-Игроки – с фиолетовым ником над головой;
- Убийцы – с красным ником над головой.

Описание
----
В начале все состоят в группе «Мирные игроки».
При ударе другого мирного игрока или игрока в режиме «PVP» Вы тоже переходите на 30 секунд в «Группу PVP».
Если вы убиваете игрока из «Мирной группы», то становитесь «Убийцей». Находясь в «Мирной группе» и атакуя «Убийцу» Вы не переходите в режим «PVP».

За каждое убийство у вас падает карма на 10 баллов.
Для того чтобы перейти обратно в «Мирную группу» – Вам надо убивать овец, коров, зомби или скелетов.
За каждого моба Ваша карма восполняется на 1 балл.
Если Вы погибаете будучи «Убийцей», то карма восполняется на 10 баллов.

Отличия групп
----
- Мирные игроки – погибая от других игроков не теряют опыт, не теряют броню, с вероятностью 95% не потеряют вещи из инвентаря;
- PVP-Игроки – погибая теряют всё;
- Убийцы – погибая теряют всё. На убийц накладываются эффекты: Замедление, Усталость, Слабость. Эффекты сохраняются пока карма меньше нуля.

Команды
----
- /pvpstatus – информция о Вашем статусе.
