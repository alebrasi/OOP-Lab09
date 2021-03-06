package it.unibo.oop.lab.lambda.ex02;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.Set;
import java.util.stream.Stream;

/**
 *
 */
public final class MusicGroupImpl implements MusicGroup {

    private final Map<String, Integer> albums = new HashMap<>();
    private final Set<Song> songs = new HashSet<>();

    @Override
    public void addAlbum(final String albumName, final int year) {
        this.albums.put(albumName, year);
    }

    @Override
    public void addSong(final String songName, final Optional<String> albumName, final double duration) {
        if (albumName.isPresent() && !this.albums.containsKey(albumName.get())) {
            throw new IllegalArgumentException("invalid album name");
        }
        this.songs.add(new MusicGroupImpl.Song(songName, albumName, duration));
    }

    @Override
    public Stream<String> orderedSongNames() {
        List<String> tmp = new LinkedList<>();
        this.songs.forEach(s -> tmp.add(s.getSongName()));
        Collections.sort(tmp, (String s1, String s2) -> s1.compareTo(s2));
        return tmp.stream();
    }

    @Override
    public Stream<String> albumNames() {
        List<String> tmp = this.albums.keySet().stream().collect(toList());
        Collections.sort(tmp, (String s1, String s2) -> s1.compareTo(s2));
        return tmp.stream();
    }

    @Override
    public Stream<String> albumInYear(final int year) {
        List<String> tmp = new ArrayList<>();
        this.albums.forEach((k, v) -> {
            if (v == year) {
                tmp.add(k);
            }
        });
        return tmp.stream();
    }

    @Override
    public int countSongs(final String albumName) {
        return (int) this.songs.stream().filter(s -> s.getAlbumName().orElse("").equals(albumName)).count();
    }

    @Override
    public int countSongsInNoAlbum() {
        return (int) this.songs.stream().filter(s -> s.getAlbumName().equals(Optional.empty())).count();
    }

    @Override
    public OptionalDouble averageDurationOfSongs(final String albumName) {
        return this.songs.stream().filter(s -> s.getAlbumName().orElse("").equals(albumName)).mapToDouble(x -> x.getDuration()).average();
    }

    @Override
    public Optional<String> longestSong() {
        return this.songs.stream().map(s -> s).max(Comparator.comparing(s -> s.getDuration())).map(s -> s.getSongName());
    }

    @Override
    public Optional<String> longestAlbum() {
        Map<Double, String> tmp = new HashMap<>();
        this.albums.keySet().forEach(s -> tmp.put(songs.stream().filter(t -> s.equals(t.albumName.orElse(""))).collect(toList()).stream().mapToDouble(l -> l.getDuration()).sum(), s));
        return Optional.of(tmp.get(tmp.keySet().stream().max(Comparator.comparing(i -> i)).get()));
    }

    private static final class Song {

        private final String songName;
        private final Optional<String> albumName;
        private final double duration;
        private int hash;

        Song(final String name, final Optional<String> album, final double len) {
            super();
            this.songName = name;
            this.albumName = album;
            this.duration = len;
        }

        public String getSongName() {
            return songName;
        }

        public Optional<String> getAlbumName() {
            return albumName;
        }

        public double getDuration() {
            return duration;
        }

        @Override
        public int hashCode() {
            if (hash == 0) {
                hash = songName.hashCode() ^ albumName.hashCode() ^ Double.hashCode(duration);
            }
            return hash;
        }

        @Override
        public boolean equals(final Object obj) {
            if (obj instanceof Song) {
                final Song other = (Song) obj;
                return albumName.equals(other.albumName) && songName.equals(other.songName)
                        && duration == other.duration;
            }
            return false;
        }

        @Override
        public String toString() {
            return "Song [songName=" + songName + ", albumName=" + albumName + ", duration=" + duration + "]";
        }

    }

}
