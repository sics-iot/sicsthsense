PLAY_USER=play
PLAY_HOME=/home2/play
PLAY_HOST=sense.sics.se
PLAY_KEY=-i $(HOME)/.ssh/play-key
SSH=ssh $(PLAY_KEY)

ifdef test
CCHECK=-n
endif

ifndef key
key=$(USER)
endif

RSYNC=rsync $(CCHECK) -avz --delete -progress \
	-e "$(SSH)" \
	--exclude '.svn' \
	--exclude '.git' \
	--exclude 'Makefile*' \
	--exclude '*~' \
	--exclude '\#*\#' \
	--exclude 'target' \
	--exclude 'dist' \
	--exclude 'tmp' \
	--exclude 'conf/application.conf' \
	--exclude 'project/project' \
	--exclude 'conf/evolutions' \
	--exclude 'project/target'

.PHONY: upload update all

all:
	@echo "Connected to Play at $(PLAY_HOST)"
	@echo "Run 'make upload' to upload (temporary directory)"
	@echo "Run 'make update' to upload and update (development mode)"
	@echo "Run 'make upgrade' to upload and update (production mode)"
	@echo "Run 'make start' to start Play"
	@echo "Run 'make devel' to start Play in developer mode"
	@echo "Run 'make stop' to stop Play"

upload:
	$(RSYNC) ./ $(PLAY_USER)@$(PLAY_HOST):$(PLAY_HOME)/cloud-$(key)

update:	upload
	$(SSH) $(PLAY_USER)@$(PLAY_HOST) "$(PLAY_HOME)/update-play.sh $(CCHECK) -d $(key)"

upgrade:	upload
	$(SSH) $(PLAY_USER)@$(PLAY_HOST) "$(PLAY_HOME)/update-play.sh $(CCHECK) $(key)"

start:
	$(SSH) $(PLAY_USER)@$(PLAY_HOST) "$(PLAY_HOME)/start-play.sh $(CCHECK)"

devel:
	$(SSH) $(PLAY_USER)@$(PLAY_HOST) "$(PLAY_HOME)/start-play.sh $(CCHECK) -d"

stop:
	$(SSH) $(PLAY_USER)@$(PLAY_HOST) "$(PLAY_HOME)/stop-play.sh $(CCHECK)"
