/*
 * Copyright (c) 2018 - 2020 by Botorabi. All rights reserved.
 * https://github.com/botorabi/HomieCenter
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package net.vrfun.homiecenter.model;

import org.springframework.lang.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Container for holding device statistics values
 *
 * @author          boto
 * Creation Date    25th September 2018
 */
public class DeviceStatsValues {

    public static class Stats {

        private int grid;
        private List<Integer> values;

        public Stats(int grid, @NonNull final List<Integer> values) {
            this.grid = grid;
            this.values = values;
        }

        public int getGrid() {
            return grid;
        }

        @NonNull
        public List<Integer> getValues() {
            return values;
        }
    }

    private List<Stats> stats = new ArrayList<>();

    public void setStats(@NonNull final List<Stats> stats) {
        this.stats = stats;
    }

    @NonNull
    public List<Stats> getStats() {
        return stats;
    }

    public void addStats(int grid, @NonNull final List<Integer> values) {
        this.stats.add(new Stats(grid, values));
    }
}
