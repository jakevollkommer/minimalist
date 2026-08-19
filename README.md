# Minimalist

Hide the scenery you can't interact with. Curated, per-content toggles remove decorative
objects, wandering NPCs, projectiles, and HUD clutter so only the things that matter stay
on screen.

No ID inputs — every toggle maps to hardcoded, human-reviewed ID sets sourced from the
game cache, and nothing is ever removed from the scene: hiding happens at the renderer,
so hidden objects stay hoverable, clickable, and visible to other plugins.

## Supported content

### Guardians of the Rift

| Toggle | Hides |
|---|---|
| Abyss scenery | Whale-fall, kelp, lace, fossils, statues, and the rest of the abyss backdrop |
| Inactive guardian statues | All guardian statues except the two active ones and any whose portal talisman you hold — visibility follows the altar rotation instantly, and hidden statues also lose their menu entries so you can't misclick them |
| Guardian remains | Small guardian parts and depleted remains; Large/Huge/Fallen (mineable) always show |
| Essence piles | Elemental and catalytic essence piles |
| Barriers and cells | Barriers (both width variants), the weak cells table, and the guides |
| Barrier hitsplats | The hitsplats and health bars drawn on the barriers |
| Altar scenery | Decoration inside all twelve runecrafting altars — pillars, glows, rubble, plants, corpses, ghosts — region-gated so these shared world IDs never disappear anywhere else |
| Entrance scenery | Lobby pillars, rubble, skeleton, cart, and fountain (never the agility shortcut) |
| Rain | The rain effect inside the temple |
| Abyssal creatures | Abyssal guardians, walkers, and leeches |
| Summoned guardians | Catalytic and elemental guardians summoned by players |
| Apprentices | Apprentices Tamara, Cordelia, and Felix |
| Rick | Rick |
| Projectiles | Projectiles from creatures attacking the barriers and guardian |
| HUD elements | Portal timer, guardian counter, portal location text |

## How it works

- All hiding goes through RuneLite's `RenderCallback`: static scenery is filtered when the
  scene uploads (toggle changes apply with one quick reload), and animated objects — the
  guardian statues — are filtered per frame, so active/inactive changes show instantly.
- Active altars are read from the game's own HUD update script, the same source the
  in-game HUD uses.
- Everything functional is deliberately excluded: mineable remains, the passable barrier,
  agility shortcuts, ladders, altars, portals, and exit markers always render.

## Code layout

- `MinimalistPlugin` — lifecycle, events, and the render callback
- `gotr/` — arena scenery, NPCs, HUD components, and guardian statue data
- `altars/` — one file per runecrafting altar, plus shared altar decoration and the
  scene-to-altar resolution

## License

BSD 2-Clause
