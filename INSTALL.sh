# build the database
cat buildDB.sql | mysql -u root -p

# move the default play config file for play, do not clobber
cp -n web/conf/application.conf.default web/conf/application.conf

