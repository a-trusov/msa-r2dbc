# MSA demo project (ARC-015)
Default login/password: user/pass

**Homework-3**:
1.Install postgresql cluster in kubernetes

	- Install Helm 3 https://helm.sh/docs/intro/install/
	- Install postgresql https://hub.helm.sh/charts/bitnami/postgresql
	- kubectl get secret --namespace default postgresql -o jsonpath="{.data.postgresql-password}" | base64 --decode
	- kubectl get services
	- Connect to the DB from Intellij IDEA Database Tool Window using ${CLUSTER-IP} of postgresql
	- ClustedIP access to postgresql from local PC stopped working for me later. I changed type to NodePort and got a host:port like this: minikube service postgresql --url
		- More details here: https://docs.bitnami.com/kubernetes/infrastructure/postgresql-ha/configuration/expose-service/
		- And here: https://kubernetes.io/docs/concepts/services-networking/service/#publishing-services-service-types
	- You can do the same for dashboard in order to always be able to access it without 'minikube dashboard'
	
2.Implement API of easy-wallet using Spring WebFlux, Spring Data R2DBC and Postgresql

	- WalletController with methods to create/get/replenish/delete a wallet 
	and transfer from one wallet to another
	- Write an API test and test it against the real postgresql database
	- Configure test to use embedded h2 by default and use postgresql when profile is activated
