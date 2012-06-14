PLAY_USER=play
PLAY_HOME=/home2/play
PLAY_HOST=sense.sics.se

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
	--exclude 'dist' \
	--exclude 'tmp' \
	--exclude 'conf/application.conf' \
	--exclude 'conf/evolutions' \
	--exclude 'project/project' \
	--exclude 'project/target'

.PHONY: upload update all

all:
	@echo "Run 'make upload' to upload Play to $(PLAY_HOST)"
	@echo "Run 'make update' to upload and update Play at $(PLAY_HOST)"
	@echo "Run 'make start' to start Play at $(PLAY_HOST)"
	@echo "Run 'make devel' to start Play in developer mode at $(PLAY_HOST)"
	@echo "Run 'make stop' to stop Play at $(PLAY_HOST)"

upload:
	$(RSYNC) ./ $(PLAY_USER)@$(PLAY_HOST):$(PLAY_HOME)/cloud-$(key)

update:	upload
	ssh $(PLAY_USER)@$(PLAY_HOST) "$(PLAY_HOME)/update-play.sh $(CCHECK) $(key)"

start:
	ssh $(PLAY_USER)@$(PLAY_HOST) "$(PLAY_HOME)/start-play.sh $(CCHECK)"

devel:
	ssh $(PLAY_USER)@$(PLAY_HOST) "$(PLAY_HOME)/start-play.sh $(CCHECK) -d"

stop:
	ssh $(PLAY_USER)@$(PLAY_HOST) "$(PLAY_HOME)/stop-play.sh $(CCHECK)"
