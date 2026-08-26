# Bundle provenance report

format_version: B02-B04-v2
archive: airflow-bundle-airflow-3.2.1-20260827_082235.7z
archive_bytes: 1232625
archive_sha256: 1ae89f255be3ff16cf0f4d0a537460b2f7edfb227f05bea517011036323274e1
source_git_commit: ed4a2d8f4abe9f24310a39e481a3d75a229c5eb1
manifest_source_git_commit: ed4a2d8f4abe9f24310a39e481a3d75a229c5eb1
airflow_target_version: 3.2.1
payload_source_file_count: 113
payload_packaged_file_count: 113
payload_diff_count: 0
payload_diff_sha256: e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855
payload_comparison: PASS
tar_member_count: 114
tar_sha256: 3bec444cece69b81957da910bdc541c65edcd32d14f894bf19dace73609380c4
tar_members_sha256: e526419d172f8402a1dba86cf15a8f48134e3bc083271ebdb14e28a2ec19fcae
new_extraction_verification: PASS
sha256sums: airflow-bundle-airflow-3.2.1-20260827_082235.SHA256SUMS

## External 7z member tree hashes

| Member | Kind | Files | Bytes | Tree SHA-256 |
|---|---|---:|---:|---|
| `batch-admin-reference` | directory | 70 | 2812166 | `1f7ee1f80e07bbcda5d47ef699de8c259591440634cbe18f702c55aca5220441` |
| `docs` | directory | 36 | 921721 | `9a45aedf25ea1f877d02da8a4d122215e5ab8bf0dcff2d6a36fc6521ad5a492a` |
| `python_batch_agent` | directory | 10 | 127530 | `b6fde790299d94d530fddc82cf7d92fcbe1b3eae02cc2c2257faf6e4cc28cf32` |
| `spring-batch-reference` | directory | 8 | 48477 | `608ee56e799dbb6d7830746089b04cfbcb3e8fafc236ea9c11e2217ff21044ad` |
| `sql` | directory | 1 | 21725 | `3b9e0ee01ae561194c6f1255dde19da6062b1aa579490ba00ef67ba2fed51c4e` |

## Sorted internal tar member list

```text
airflow-bundle/README.md
airflow-bundle/airflow_target_profile.json
airflow-bundle/bundle_manifest.json
airflow-bundle/config/catalog.d/README.md
airflow-bundle/config/catalog.d/_TEMPLATE.yaml.example
airflow-bundle/config/catalog.d/mvno_full_chain.yaml
airflow-bundle/config/catalog.d/mvno_full_chain_pybatch.yaml
airflow-bundle/config/catalog.d/streamsets_autogen_dispatchers.yaml
airflow-bundle/config/catalog.d/streamsets_dispatchers.yaml
airflow-bundle/config/catalog.d/watchdog_daily_flow.yaml.example
airflow-bundle/config/catalog.d/wireline_master_flow.yaml
airflow-bundle/config/catalog.d/wireline_monthly_single_flow.yaml
airflow-bundle/config/env_overrides/prd.yaml.example
airflow-bundle/config/image_manifest.json
airflow-bundle/config/pipeline_registry.yaml
airflow-bundle/config/streamsets_autogen_policy.json
airflow-bundle/config/variables.dev.json
airflow-bundle/config/variables.json
airflow-bundle/config/variables.prd.json
airflow-bundle/config/variables.schema.json
airflow-bundle/dags/.airflowignore
airflow-bundle/dags/_scaffold/__init__.py
airflow-bundle/dags/_scaffold/batch_job_factory.py
airflow-bundle/dags/_scaffold/dispatcher_factory.py
airflow-bundle/dags/_scaffold/flow_factory.py
airflow-bundle/dags/_scaffold/python_batch_factory.py
airflow-bundle/dags/_scaffold/window_dispatcher_factory.py
airflow-bundle/dags/cm_ops_edit_manifest.py
airflow-bundle/dags/cm_ops_reconcile_activation.py
airflow-bundle/dags/cm_ops_sync_catalog_from_db.py
airflow-bundle/dags/cm_ops_sync_python_autogen.py
airflow-bundle/dags/cm_ops_sync_spring_autogen.py
airflow-bundle/dags/common/__init__.py
airflow-bundle/dags/common/activation_reconcile.py
airflow-bundle/dags/common/airflow_compat.py
airflow-bundle/dags/common/batch_params.py
airflow-bundle/dags/common/batch_runtime.py
airflow-bundle/dags/common/callbacks.py
airflow-bundle/dags/common/catalog_from_db.py
airflow-bundle/dags/common/catalog_schedule.py
airflow-bundle/dags/common/catalog_writer.py
airflow-bundle/dags/common/dag_task_map_sync.py
airflow-bundle/dags/common/dag_task_map_trigger.py
airflow-bundle/dags/common/datasets.py
airflow-bundle/dags/common/defaults.py
airflow-bundle/dags/common/env_overrides.py
airflow-bundle/dags/common/flow_catalog_spec.py
airflow-bundle/dags/common/helpers.py
airflow-bundle/dags/common/manifest_edit.py
airflow-bundle/dags/common/security_utils.py
airflow-bundle/dags/ops_edit_catalog_schedule.py
airflow-bundle/dags/ops_read_catalog_def.py
airflow-bundle/dags/ops_sync_dag_task_map.py
airflow-bundle/dags/ops_sync_streamsets_autogen.py
airflow-bundle/dags/ops_write_catalog_def.py
airflow-bundle/dags/streamsets/__init__.py
airflow-bundle/dags/streamsets/dispatcher_engine.py
airflow-bundle/dags/streamsets/dynamic_dispatcher.py
airflow-bundle/dags/streamsets/window_coordinator.py
airflow-bundle/dags/zz_runtime_catalogs.py
airflow-bundle/plugins/__init__.py
airflow-bundle/plugins/dependency_count.py
airflow-bundle/plugins/hooks/__init__.py
airflow-bundle/plugins/hooks/sdc_pool_hook.py
airflow-bundle/plugins/hooks/sdc_shell_hook.py
airflow-bundle/plugins/multi_cron_timetable_plugin.py
airflow-bundle/plugins/operators/__init__.py
airflow-bundle/plugins/operators/python_batch_operator.py
airflow-bundle/plugins/operators/python_batch_remote_operator.py
airflow-bundle/plugins/operators/spring_batch_operator.py
airflow-bundle/plugins/operators/streamset_pool_operator.py
airflow-bundle/plugins/sensors/__init__.py
airflow-bundle/plugins/sensors/sdc_pool_sensor.py
airflow-bundle/plugins/sensors/wrk_hst_count_sensor.py
airflow-bundle/plugins/timetables/__init__.py
airflow-bundle/plugins/timetables/multi_cron_timetable.py
airflow-bundle/requirements/airflow-3.2.1.txt
airflow-bundle/scripts/_pyenv.sh
airflow-bundle/scripts/airflow_target_bundle.py
airflow-bundle/scripts/assign_tmp_pools.py
airflow-bundle/scripts/batch_admin_rollback_preflight.sh
airflow-bundle/scripts/build_guide_html.py
airflow-bundle/scripts/bundle_provenance.py
airflow-bundle/scripts/clean_dag_history.sh
airflow-bundle/scripts/collect_import_diag.sh
airflow-bundle/scripts/deploy.sh
airflow-bundle/scripts/detect_composition_conflicts.py
airflow-bundle/scripts/diagnose.sh
airflow-bundle/scripts/diagnose_pybatch_callables.py
airflow-bundle/scripts/extract_python_batch_manifest.py
airflow-bundle/scripts/extract_streamsets_crontab.py
airflow-bundle/scripts/generate_manifest.sh
airflow-bundle/scripts/generate_spring_batch_catalog.py
airflow-bundle/scripts/import_variables.sh
airflow-bundle/scripts/materialize_catalog_def.py
airflow-bundle/scripts/materialize_schedule.py
airflow-bundle/scripts/package.sh
airflow-bundle/scripts/package_hygiene_gate.sh
airflow-bundle/scripts/reconcile_chain_ownership.py
airflow-bundle/scripts/regression_check.py
airflow-bundle/scripts/rollback.sh
airflow-bundle/scripts/scaffold_flow.py
airflow-bundle/scripts/scaffold_loaders.py
airflow-bundle/scripts/setup_ssh_key.sh
airflow-bundle/scripts/stage_release_upload.sh
airflow-bundle/scripts/suggest_lineage_gates.py
airflow-bundle/scripts/sync_dag_task_map.py
airflow-bundle/scripts/sync_remote_manifests.py
airflow-bundle/scripts/validate_catalog.py
airflow-bundle/scripts/verify_bundle.sh
airflow-bundle/scripts/verify_bundle_provenance.sh
airflow-bundle/scripts/verify_import_pointer_freshness.py
airflow-bundle/scripts/verify_parallel_run.py
airflow-bundle/scripts/verify_variables.py
```

## Verification contract

The outer archive was extracted before this report was written. The internal tar was safely extracted, its Airflow target, manifest commit, and hashes were checked, and the product payload was compared byte-for-byte with the selected target staging tree. The SHA256SUMS sidecar covers the archive, the report, and every extracted regular file; its member-root comments cover external 7z roots.
