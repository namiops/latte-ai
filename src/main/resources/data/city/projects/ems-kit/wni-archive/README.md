# wni-archive - A script to archive Weather News API data

See [Powerporter - Weather News API](https://confluence.tri-ad.tech/x/fBjoNg) for more information.

## Usage

```shell
wni_archive
wni_archive --dir data/
```

## Instructions

This project uses [Rye](https://rye.astral.sh/) for package management.

```shell
cp .env.example .env
(Update .env with WNI_ID and WNI_API_KEY)

rye sync

# Test
rye run pytest

# Archive WNI data in the local filesystem
rye run python -m wni_archive --dir data

# Archive WNI data in the PostgreSQL
rye run python -m wni_archive
```

## Database configuration

This command create a table named `wni_archive_raw` automatically.
If you need to delete the table, run the following command.

```shell
psql -h localhost -U postgres -d pwp-db -c "DROP TABLE wni_archive_raw"
```

## Bazel integration

Build and test

```shell
bazel build //projects/ems-kit/wni-archive/...
bazel test //projects/ems-kit/wni-archive/tests:test_wni_archive 
cp .env ../../../bazel-bin/projects/ems-kit/wni-archive/src/wni_archive/wni_archive_bin.runfiles/wp_wcm_city/
bazel run //projects/ems-kit/wni-archive/src/wni_archive:wni_archive_bin
```

Create image

```shell
bazel run //projects/ems-kit/wni-archive/src/wni_archive:image.load
```


## Terminology

* wx: Weather forecast data
* anlsis: Observed and Analyzed forecast data
