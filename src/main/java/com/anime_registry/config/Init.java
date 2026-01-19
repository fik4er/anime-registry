package com.anime_registry.config;

import com.anime_registry.entity.Anime;
import com.anime_registry.entity.Genre;
import com.anime_registry.entity.Role;
import com.anime_registry.entity.User;
import com.anime_registry.repository.AnimeRepository;
import com.anime_registry.repository.GenreRepository;
import com.anime_registry.repository.RoleRepository;
import com.anime_registry.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class Init implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AnimeRepository animeRepository;
    private final GenreRepository genreRepository;
    private final PasswordEncoder passwordEncoder;
    private final String defaultPassword;

    @Autowired
    public Init(UserRepository userRepository, RoleRepository roleRepository, AnimeRepository animeRepository, GenreRepository genreRepository, PasswordEncoder passwordEncoder, @Value("${app.default.password:topsecret}") String defaultPassword) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.animeRepository = animeRepository;
        this.genreRepository = genreRepository;
        this.passwordEncoder = passwordEncoder;
        this.defaultPassword = defaultPassword;
        log.info("Init компонент инициализирован");
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("Запуск инициализации начальных данных");
        initRoles();
        initUsers();
        initGenres();
        initAnime();
        log.info("Инициализация начальных данных завершена");
    }

    private void initRoles() {
        if (roleRepository.count() == 0) {
            log.info("Создание ролей");
            Role userRole = new Role(UserRoles.USER);
            Role adminRole = new Role(UserRoles.ADMIN);
            roleRepository.saveAll(List.of(userRole, adminRole));
            log.info("Роли созданы: USER, ADMIN");
        } else {
            log.info("Роли уже существуют");
        }
    }

    private void initUsers() {
        if (userRepository.count() == 0) {
            log.info("Создание тестовых пользователей");
            initAdmin();
            initNormalUser();
            log.info("Тестовые пользователи созданы");
        } else {
            log.info("Пользователи уже существуют");
        }
    }

    private void initAdmin() {
        Role adminRole = roleRepository.findByName(UserRoles.ADMIN)
                .orElseThrow(() -> new RuntimeException("Роль ADMIN не найдена!"));
        Role userRole = roleRepository.findByName(UserRoles.USER)
                .orElseThrow(() -> new RuntimeException("Роль USER не найдена!"));

        User admin = new User();
        admin.setEmail("admin@example.com");
        admin.setPasswordHash(passwordEncoder.encode(defaultPassword));
        admin.setIsEmailVerified(true);
        admin.getRoles().add(adminRole);
        admin.getRoles().add(userRole);

        userRepository.save(admin);
        log.info("Создан администратор: admin@example.com");
    }

    private void initNormalUser() {
        Role userRole = roleRepository.findByName(UserRoles.USER)
                .orElseThrow(() -> new RuntimeException("Роль USER не найдена!"));

        User user = new User();
        user.setEmail("user@example.com");
        user.setPasswordHash(passwordEncoder.encode(defaultPassword));
        user.setIsEmailVerified(true);
        user.getRoles().add(userRole);

        userRepository.save(user);
        log.info("Создан обычный пользователь: user@example.com");
    }

    private void initGenres() {
        if (genreRepository.count() == 0) {
            log.info("Создание базовых жанров");

            List<Genre> genres = List.of(
                    new Genre("Комедия"),
                    new Genre("Романтика"),
                    new Genre("Драма"),
                    new Genre("Фантастика"),
                    new Genre("Фэнтези"),
                    new Genre("Школа"),
                    new Genre("Сверхъестественное"),
                    new Genre("Меха"),
                    new Genre("Сёнен"),
                    new Genre("Сэйнэн"),
                    new Genre("Детектив"),
                    new Genre("Повседневность"),
                    new Genre("Боевик"),
                    new Genre("Приключения"),
                    new Genre("Ужасы")
            );

            genreRepository.saveAll(genres);
            log.info("Базовые жанры созданы: {}", genres.size());
        } else {
            log.info("Жанры уже существуют");
        }
    }

    private void initAnime() {
        if (animeRepository.count() == 0) {
            log.info("Создание базовых аниме");

            Genre comedy = genreRepository.findByName("Комедия").orElse(null);
            Genre school = genreRepository.findByName("Школа").orElse(null);
            Genre romance = genreRepository.findByName("Романтика").orElse(null);
            Genre drama = genreRepository.findByName("Драма").orElse(null);
            Genre supernatural = genreRepository.findByName("Сверхъестественное").orElse(null);
            Genre sciFi = genreRepository.findByName("Фантастика").orElse(null);
            Genre action = genreRepository.findByName("Боевик").orElse(null);
            Genre horror = genreRepository.findByName("Ужасы").orElse(null);
            Genre shonen = genreRepository.findByName("Сёнен").orElse(null);
            Genre mecha = genreRepository.findByName("Меха").orElse(null);
            Genre detective = genreRepository.findByName("Детектив").orElse(null);

            List<Anime> animeList = List.of(
                    createAdzumanga(),
                    createScientificProofOfLove(comedy, school, romance),
                    createYourName(drama, romance, supernatural, sciFi),
                    createMyHeroAcademia(action, school, shonen, comedy),
                    createAttackOnTitan(action, drama, shonen),
                    createTokyoGhoul(action, drama, horror, supernatural),
                    createDeathNote(drama, supernatural, detective),
                    createEvangelion(mecha, action, drama, sciFi)
            );

            animeRepository.saveAll(animeList);
            log.info("Базовые аниме созданы: {}", animeList.size());
        } else {
            log.info("Аниме уже существуют");
        }
    }

    private Anime createAdzumanga() {
        Anime anime = new Anime();
        anime.setTitleRu("Адзуманга");
        anime.setTitleJp("アズマンガ大王");
        anime.setTitleEn("Azumanga Daioh");
        anime.setYear(2002);
        anime.setAgeRating("12+");
        anime.setThumbnailPath("/images/adzu.png");
        anime.setDescription("Весёлая повседневная комедия о школьной жизни, рассказывающая о приключениях студентов старшей школы, их учителях и друзьях. Сериал полон забавных ситуаций и харизматичных персонажей.");

        Genre comedy = genreRepository.findByName("Комедия").orElse(null);
        Genre school = genreRepository.findByName("Школа").orElse(null);
        Genre everydayLife = genreRepository.findByName("Повседневность").orElse(null);

        if (comedy != null) anime.getGenres().add(comedy);
        if (school != null) anime.getGenres().add(school);
        if (everydayLife != null) anime.getGenres().add(everydayLife);

        return anime;
    }

    private Anime createScientificProofOfLove(Genre comedy, Genre school, Genre romance) {
        Anime anime = new Anime();
        anime.setTitleRu("Научное доказательство любви");
        anime.setTitleJp("恋は科学の証明");
        anime.setTitleEn("Scientific Proof of Love");
        anime.setYear(2021);
        anime.setAgeRating("12+");
        anime.setThumbnailPath("/images/nauch-love.png");
        anime.setDescription("История о науке и любви, где главный герой пытается доказать существование любви с помощью научного подхода. Захватывающий романтический сериал с элементами комедии и научных экспериментов.");

        if (comedy != null) anime.getGenres().add(comedy);
        if (school != null) anime.getGenres().add(school);
        if (romance != null) anime.getGenres().add(romance);

        return anime;
    }

    private Anime createYourName(Genre drama, Genre romance, Genre supernatural, Genre sciFi) {
        Anime anime = new Anime();
        anime.setTitleRu("Твоё имя");
        anime.setTitleJp("君の名は。");
        anime.setTitleEn("Your Name");
        anime.setYear(2016);
        anime.setAgeRating("12+");
        anime.setThumbnailPath("/images/your-name.png");
        anime.setDescription("Фантастическая драма о подростках, которые внезапно начинают меняться телами. Потрясающая история о любви, судьбе и связи между двумя людьми, разделёнными расстоянием и временем.");

        if (drama != null) anime.getGenres().add(drama);
        if (romance != null) anime.getGenres().add(romance);
        if (supernatural != null) anime.getGenres().add(supernatural);
        if (sciFi != null) anime.getGenres().add(sciFi);

        return anime;
    }

    private Anime createMyHeroAcademia(Genre action, Genre school, Genre shonen, Genre comedy) {
        Anime anime = new Anime();
        anime.setTitleRu("Моя геройская академия");
        anime.setTitleJp("僕のヒーローアカデミア");
        anime.setTitleEn("My Hero Academia");
        anime.setYear(2016);
        anime.setAgeRating("12+");
        anime.setThumbnailPath("/images/mha.png");
        anime.setDescription("В мире, где 80% людей обладают сверхспособностями, Изуку Мидория родился без них. Но это не мешает ему мечтать стать величайшим героем.");

        if (action != null) anime.getGenres().add(action);
        if (school != null) anime.getGenres().add(school);
        if (shonen != null) anime.getGenres().add(shonen);
        if (comedy != null) anime.getGenres().add(comedy);

        return anime;
    }

    private Anime createAttackOnTitan(Genre action, Genre drama, Genre shonen) {
        Anime anime = new Anime();
        anime.setTitleRu("Атака титанов");
        anime.setTitleJp("進撃の巨人");
        anime.setTitleEn("Attack on Titan");
        anime.setYear(2013);
        anime.setAgeRating("16+");
        anime.setThumbnailPath("/images/aot.png");
        anime.setDescription("Человечество живет внутри городов, окруженных огромными стенами, защищающими их от гигантских человекоподобных существ — титанов. Когда один из титанов прорывает стену, борьба за выживание начинается anew.");

        if (action != null) anime.getGenres().add(action);
        if (drama != null) anime.getGenres().add(drama);
        if (shonen != null) anime.getGenres().add(shonen);

        return anime;
    }

    private Anime createTokyoGhoul(Genre action, Genre drama, Genre horror, Genre supernatural) {
        Anime anime = new Anime();
        anime.setTitleRu("Токийский гуль");
        anime.setTitleJp("東京喰種トーキョーグール");
        anime.setTitleEn("Tokyo Ghoul");
        anime.setYear(2014);
        anime.setAgeRating("18+");
        anime.setThumbnailPath("/images/tokyo-ghoul.png");
        anime.setDescription("Кен Канеки — обычный студент, который после встречи с гулем (существом, питающимся человеческим плотью) становится полукровкой. Теперь ему приходится балансировать между двумя мирами.");

        if (action != null) anime.getGenres().add(action);
        if (drama != null) anime.getGenres().add(drama);
        if (horror != null) anime.getGenres().add(horror);
        if (supernatural != null) anime.getGenres().add(supernatural);

        return anime;
    }

    private Anime createDeathNote(Genre drama, Genre supernatural, Genre detective) {
        Anime anime = new Anime();
        anime.setTitleRu("Тетрадь смерти");
        anime.setTitleJp("デスノート");
        anime.setTitleEn("Death Note");
        anime.setYear(2006);
        anime.setAgeRating("16+");
        anime.setThumbnailPath("/images/death-note.png");
        anime.setDescription("Старшеклассник Лайт Ягами находит сверхъестественную тетрадь, которая убивает любого, чье имя в неё записано. Он решает очистить мир от преступников, но за ним начинает охотиться гениальный детектив L.");

        if (drama != null) anime.getGenres().add(drama);
        if (supernatural != null) anime.getGenres().add(supernatural);
        if (detective != null) anime.getGenres().add(detective);

        return anime;
    }

    private Anime createEvangelion(Genre mecha, Genre action, Genre drama, Genre sciFi) {
        Anime anime = new Anime();
        anime.setTitleRu("Евангелион");
        anime.setTitleJp("新世紀エヴァンゲリオン");
        anime.setTitleEn("Neon Genesis Evangelion");
        anime.setYear(1995);
        anime.setAgeRating("16+");
        anime.setThumbnailPath("/images/eva.png");
        anime.setDescription("В постапокалиптическом мире организация NERV использует гигантских биомеханических роботов (Евангелионов), чтобы сражаться с загадочными существами — Ангелами.");

        if (mecha != null) anime.getGenres().add(mecha);
        if (action != null) anime.getGenres().add(action);
        if (drama != null) anime.getGenres().add(drama);
        if (sciFi != null) anime.getGenres().add(sciFi);

        return anime;
    }
}