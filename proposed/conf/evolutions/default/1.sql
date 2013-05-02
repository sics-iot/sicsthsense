# Modify unique constraint of resources to include parent_id in addition to owner_id and label

# --- !Ups
ALTER TABLE resources DROP INDEX uq_resources_1;
ALTER TABLE resources ADD CONSTRAINT uq_resources_1 UNIQUE (owner_id,parent_id,label);

# --- !Downs
ALTER TABLE resources DROP INDEX uq_resources_1;

