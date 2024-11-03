# Spark image for running Hudi
These versions of packages have been tested and verified to work - change them at your own peril. Many packages, e.g. Hadoop 3.3.6, have complicated bugs and will throw ClassNotFoundException.

If changing any of the `hadoop-*` jars, they *must* have the same version as all other `hadoop-*` jars.


## conda setup

The Dockerfile is edited based on the following to allow users to create their own conda environments for notebooks,

- [Customizing User Environment — Zero to JupyterHub with Kubernetes documentation](https://z2jh.jupyter.org/en/latest/jupyterhub/customizing/user-environment.html#allow-users-to-create-their-own-conda-environments-for-notebooks)
- [How to make created custom (conda) environments persist across user sessions in Jupyter Hub on AWS EKS cluster - JupyterHub / Zero to JupyterHub on Kubernetes - Jupyter Community Forum](https://discourse.jupyter.org/t/how-to-make-created-custom-conda-environments-persist-across-user-sessions-in-jupyter-hub-on-aws-eks-cluster/12111/6)
