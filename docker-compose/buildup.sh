echo
echo '# prepare: builds stuff'
mvn -f ../pom.xml clean install

echo
echo '# prepare: cleans src/main/docker/maven/ jars'
mkdir -p ../instagram-client/src/main/docker/maven/
find ../../instagram-client/src/main/docker/maven/ \
  -type f \
  -iname '*.jar' \
  -exec bash -c 'echo clean {} && rm -f {}' \;


echo
echo '# prepare: src/main/docker/maven/ jars'

cp ../instagram-client/target/*.jar ../instagram-client/src/main/docker/maven || echo failed

docker-compose up --build -d
