-- Player table
CREATE TABLE player (
                        player_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        name VARCHAR(255) NOT NULL,
                        creation_date TIMESTAMP NOT NULL
);

-- Round table
CREATE TABLE round (
                       round_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       player_id BIGINT NOT NULL,
                       round_date DATE NOT NULL,
                       total_strokes_gained DECIMAL(5,2),
                       creation_date TIMESTAMP NOT NULL,
                       FOREIGN KEY (player_id) REFERENCES player(player_id)
);

-- HoleStat table
CREATE TABLE hole_stat (
                           hole_stat_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                           round_id BIGINT NOT NULL,
                           hole_number INTEGER NOT NULL CHECK (hole_number BETWEEN 1 AND 18),
                           par INTEGER NOT NULL CHECK (par BETWEEN 3 AND 5),
                           strokes_gained_ott DECIMAL(4,2),
                           strokes_gained_approach DECIMAL(4,2),
                           strokes_gained_short_game DECIMAL(4,2),
                           strokes_gained_putting DECIMAL(4,2),
                           creation_date TIMESTAMP NOT NULL,
                           FOREIGN KEY (round_id) REFERENCES round(round_id) ON DELETE CASCADE,
                           CONSTRAINT unique_hole_per_round UNIQUE (round_id, hole_number)
);

-- Indexes for better query performance
CREATE INDEX idx_round_player ON round(player_id);
CREATE INDEX idx_hole_stat_round ON hole_stat(round_id);