**Steps to Deploy the Docker container of ICRM on Ubuntu System**

**Note:**

Demo data must load first. For that, the MySQL5.7 version must be installed, and later, the database with the below Character set and collation must be imported. The SQL file freemiumdemodata.sql, which is available in the source code, must be imported.
**Create the database in name:** crm_demo_data
**Character set:** utf8
**Collation:** utf8_general_ci

Change the database connection in file entityengine.xml in localmysql connector.

![image](https://github.com/user-attachments/assets/6fb246f4-f9cb-443c-830c-0b92f4bab17b)

**Step 1: Download ICRM software from github**

We recommend using git download latest iCRM software. first, make sure you have git client installed on system after that check out the latest build from the GitHub repository.
# apt install git
# git@github.com:aiintelekt/iCRM.git

**Step 2: Install Docker on Ubuntu Linux**

Follow the below commands one by one and install the docker.

# sudo apt-get update
# sudo apt-get install \ ca-certificates \ curl \ gnupg \ lsb-release
# sudo mkdir -p /etc/apt/keyrings
# sudo curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /etc/apt/keyrings/docker.gpg
# echo \ "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.gpg] https://download.docker.com/linux/ubuntu \ $(lsb_release -cs) stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null
# sudo apt-get update
# sudo apt-get install docker-ce docker-ce-cli containerd.io
# sudo docker –version
# systemctl start docker
# systemctl enable docker

**Step 3: Create Docker images**

After completing the docker installation, use following command to create a docker image.
**Note:**  Already repository is cloned as mentioned in step 1, have to switch to that path and execute the below commands.
# cd /path/iCRM/
# docker build -t iCRM .


**Step 4: Create the docker container.**

Once image is created as mentioned in step 3, now using that image have to create the container. Follow the below commands
# docker run -d -p 80:8080 -p 443:8443 iCRM

**Step 5: Check the running container.**

Once the container is deployed, check whether container is running. Follow below commands.

# docker ps
CONTAINER ID   IMAGE                    COMMAND       CREATED      STATUS PORTS                                                                                      NAMES
d5d2ff3a49ec   iCRM   "/bin/bash"   4 days ago   Up 1 minute   0.0.0.0:80->8080/tcp, [::]:80->8080/tcp, 0.0.0.0:443->8443/tcp, [::]:443->8443/tcp   iCRM

**Step 6: Access the iCRM application in Browser**

Once verified the container running, later Access iCRM site in browser below given url and login credentials which is mentioned in Readme file.

URL:  http://serverIP/admin-portal/control/main

![image](https://github.com/user-attachments/assets/dca74c65-93c0-4762-bae9-7f9c22e44d49)


**Congratulation’s!** You have successfully deployed the iCRM software your Linux system.

