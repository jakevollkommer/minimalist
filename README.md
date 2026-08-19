# Minimalist

Hide the scenery you can't interact with. Curated, per-content toggles remove decorative
objects, wandering NPCs, and HUD clutter so only the things that matter stay on screen.

No ID inputs — every toggle maps to a hardcoded, human-reviewed set of IDs sourced from the
game cache.

## Supported content

### Guardians of the Rift

| Toggle | Hides |
|---|---|
| Abyss scenery | Whale-fall, kelp, lace, fossils, statues, and the rest of the abyss backdrop |
| Guardian statues | The twelve inactive Guardian of Air/Water/... statues (active ones stay) |
| Guardian remains | Guardian parts, remains, and fallen guardians (mineable) |
| Essence piles | Elemental and catalytic essence piles |
| Barriers and cells | Barriers (including their hitsplats), the weak cells table, and the guides |
| Entrance scenery | Lobby pillars, rubble, skeleton, cart, and fountain (never the agility shortcut) |
| Rain | The rain effect inside the temple |
| Abyssal creatures | Abyssal guardians, walkers, and leeches |
| Summoned guardians | Catalytic and elemental guardians summoned by players |
| Apprentices | Apprentices Tamara, Cordelia, and Felix |
| Rick | Rick |
| HUD elements | Portal timer, guardian counter, portal location text |

## Notes

- Every hidden ID is verified against the game cache to have no menu actions (with the
  exception of intentionally hideable toggles like guardian remains and essence piles).
- Objects are removed from the scene; turning a toggle off triggers a quick scene reload to
  restore them.
- More content sections are welcome — open an issue with the area and what you'd like gone.
