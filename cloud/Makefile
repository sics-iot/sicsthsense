ifdef test
CCHECK=-n
endif

ifndef key
key=$(USER)
endif

RSYNC=rsync $(CCHECK) -avz --delete -progress \
	-e ssh \
	--exclude '.svn' \
	--exclude '.git' \
	--exclude 'Makefile*' \
	--exclude '*~' \
	--exclude '\#*\#' \
	--exclude 'target' \
	--exclude 'conf/application.conf' \
	--exclude 'project/project' \
	--exclude 'project/target'

.PHONY: upload update all

all:
	@echo "Run 'make upload' to upload to sense.sics.se"
	@echo "Run 'make update' to upload and update sense.sics.se"

upload:
	$(RSYNC) ./ play@sense.sics.se:/home2/play/cloud-$(key)

update:	upload
	ssh play@sense.sics.se "/home2/play/update-play.sh $(CCHECK) $(key)"
