# NJH import delivery channel

## Choose a directory

**New recipient:** open [`v1.5.548/`](./v1.5.548/) only. Read its
`README.md` before verifying, extracting, or installing any archive.

Do not select `v1.5.314/` or `v1.5.316/` for a new import. They are retained
only so a recipient of those deliveries can read the superseded notice that
applies to the package they already received.

| Directory | Use it for | Do not use it for |
| --- | --- | --- |
| `v1.5.314/` | Reading its superseded notice for an existing recipient. | A new import or installation. |
| `v1.5.316/` | Reading its superseded notice for an existing recipient. | A new import or installation. |
| `v1.5.548/` | The current new-recipient entry point (2026-09-01 cut): v1.5.547 plus CAPACITY REFUSE handling guidance in the install and first-30-minutes documents; same archive set. | Treating package, install, or connection checks as proof of general model-task correctness. |
| `v1.5.547/` | A superseded delivery retained for its existing recipients. | Treating package, install, or connection checks as proof of general model-task correctness. |
| `v1.5.321/` | A superseded delivery retained for its existing recipients. | Treating package, install, or connection checks as proof of general model-task correctness. |

If you already received a directory, start with the notice or erratum in that
same directory. Do not replace an installed package solely because another
directory exists here; use the approved delivery instruction for the target
environment.

## When a corrective release is added

This table is the channel's release-selection record, not a project history.
Publishing a new delivery directory must update this file in the same change:

1. Mark the new directory as the **New recipient** entry point.
2. Change the preceding entry's purpose to existing-recipient guidance.
3. Keep `v1.5.314/` and `v1.5.316/` as superseded-notice-only directories.

Until this file names a newer directory as the entry point, do not infer that a
higher-numbered directory is approved for a new import.
