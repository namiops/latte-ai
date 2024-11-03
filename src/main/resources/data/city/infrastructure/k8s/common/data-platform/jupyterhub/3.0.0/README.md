# jupyterhub

## Usage

Just access https://jupyterhub.${cluster_domain}  (e.g. https://jupyterhub.agora-lab.woven-planet.tech/)

## How to keep the installed packages after culling

In the absence of access for a certain period, the pod will automatically be culled.

The home directory (`/home/jovyan`) is set to the persistent volume so if you want to keep the installed packages,
use `--user` option like `$ pip install --user <package>`

Also, we allow users to create their own conda environments for notebooks.
The conda `envs_dirs` is set to `/home/jovyan/my-conda-envs` so you can install your desired python version
using `Terminal` like the following:

```shell
# ipykernel is necessary to make the conda env available in the jupyter notebook
conda create -n py38 python=3.8 ipykernel

source activate py38

conda install <packages you want>
```

You can use the created conda env in the jupyter notebook by selecting `py38` in the `Kernel` -> `Change kernel` menu.
