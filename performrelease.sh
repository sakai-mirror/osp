#!/bin/sh
VERSION=$1
KEYSTOREPASS=$2
RELEASE=/tmp/sakairelease

rm -rf $RELEASE
mkdir $RELEASE
rm -rf /tmp/maven2
mkdir -p /tmp/maven2/org
cp -r /tmp/maven2src/org/sakaiproject /tmp/maven2/org/sakaiproject




find . -type d -name target -exec rm -rf {} \;
find . -type d -name bin -exec rm -rf {} \;
find . -type d -name m2-target  -exec rm -rf {} \;

for i in `find . -name pom.xml | grep -v .svn `
do 
  sed "s/>M2</>${VERSION}</g" $i > $i.new
  mv $i.new $i
done

for i in `find . -name .classpath | grep -v .svn `
do 
  sed "s/\/M2\//\/${VERSION}\//g" $i | sed "s/M2.jar/${VERSION}.jar/g" |  sed "s/\/SNAPSHOT\//\/${VERSION}\//g" | sed "s/SNAPSHOT.jar/${VERSION}.jar/g"  >  $i.new
  mv $i.new $i
done

for i in `find . -name runconversion.sh | grep -v .svn  `
do 
  sed "s/\/M2\//\/${VERSION}\//g" $i | sed "s/M2.jar/${VERSION}.jar/g" | sed "s/\/SNAPSHOT\//\/${VERSION}\//g" | sed "s/SNAPSHOT.jar/${VERSION}.jar/g"  >  $i.new
  mv $i.new $i
done

cat << EOF > /tmp/tar-excludes
target
bin
m2-target
sakai*.zip
sakai*.tgz
.svn
EOF

tar --exclude-from /tmp/tar-excludes -c -v -z -f  $RELEASE/sakai-src-${VERSION}.tgz . 
mkdir -p unpack/sakai-src-${VERSION}
pushd  unpack/sakai-src-${VERSION} 
tar xvzf $RELEASE/sakai-src-${VERSION}.tgz 
cd ..
rm $RELEASE/sakai-src-${VERSION}.tgz
tar cvzf $RELEASE/sakai-src-${VERSION}.tgz .
popd 
rm -rf unpack

mkdir src
rm -rf /tmp/maven2

pushd samigo/samigo-audio

location=`pwd`

pushd master
mvn install
popd
mvn -Ppack-demo install -Dkeystore=${location}/../Sakai.keystore -Dalias=sakai -Dstorepass=${KEYSTOREPASS}
mvn install source:jar source:test-jar deploy -Dkeystore=${location}/../Sakai.keystore -Dalias=sakai -Dstorepass=${KEYSTOREPASS}
mvn javadoc:javadoc
mvn -Ptaglib taglib:taglibdocjar deploy


pushd /tmp
tar cvzf $RELEASE/mavenrepo-${VERSION}.tgz maven2
popd
pushd target/site
tar cvzf $RELEASE/sakai-javadoc-${VERSION}.tgz apidocs
popd
mv pack-demo/sakai-demo-${VERSION}.tar.gz $RELEASE/sakai-demo-${VERSION}.tar.gz
mv pack-demo/sakai-demo-${VERSION}.zip $RELEASE/sakai-demo-${VERSION}.zip
mv pack-demo/sakai-bin-${VERSION}.tar.gz $RELEASE/sakai-bin-${VERSION}.tar.gz
mv pack-demo/sakai-bin-${VERSION}.zip $RELEASE/sakai-bin-${VERSION}.zip
tar cvzf $RELEASE/taglibdocs-${VERSION}.tgz `find . -type d -name tlddoc `
pushd jsf/widgets/target/site/tlddoc 
tar cvzf $RELEASE/sakai-taglibdoc-${VERSION}.tgz . 
popd 







