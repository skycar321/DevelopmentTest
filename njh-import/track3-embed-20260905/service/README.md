# Encrypted split import

Download every file in this directory. Requires Bash (Windows Git Bash or macOS), 7-Zip (`7z` or `7zz`), and `sha256sum` or `shasum`.

Set `NJH_RELEASE_ARCHIVE_PASSWORD` only in your environment through the approved credential channel. Never save it in a script or command history. On the release workstation, load the approved environment with `set -a; . "$HOME/.njh-cli/release.env"; set +a`.

Run `bash reassemble-and-verify.sh ./verified-output` with a new output directory. The script verifies every volume before extraction and verifies the reconstructed tar.gz SHA-256 and byte count before publishing it. Extract the verified tar.gz afterward and follow `SERVICE-KIT.md` or the model kit `README.md` and `install-model.sh`. The service and model kits are separate; no GPU runtime is included.

Compare this directory's manifests against the trusted release handoff before running scripts. Checksums detect corruption, not malicious replacement of both manifests and files.
