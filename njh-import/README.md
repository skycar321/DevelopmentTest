# NJH import delivery channel

## Choose a directory

**New recipient:** open [`v1.5.321/`](./v1.5.321/) only. Read its
[`README.md`](./v1.5.321/README.md) and
[`ERRATUM.md`](./v1.5.321/ERRATUM.md) before verifying, extracting, or
installing any archive.

Do not select `v1.5.314/` or `v1.5.316/` for a new import. They are retained
only so a recipient of those deliveries can read the superseded notice that
applies to the package they already received.

| Directory | Use it for | Do not use it for |
| --- | --- | --- |
| `v1.5.314/` | Reading its superseded notice for an existing recipient. | A new import or installation. |
| `v1.5.316/` | Reading its superseded notice for an existing recipient. | A new import or installation. |
| `v1.5.321/` | The current new-recipient entry point; read both its README and erratum first. | Treating package, install, or connection checks as proof of general model-task correctness. |

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
