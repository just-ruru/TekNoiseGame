                    if(entity.solidArea.intersects(target[i].solidArea)) {
                        if(target[i] != entity) {
                            entity.collisionOn = true;
                            index = i;
                        }
                    }
