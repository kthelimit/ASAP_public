package sky.project;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import sky.project.DTO.StockDTO;
import sky.project.Entity.*;
import sky.project.Repository.*;
import sky.project.Service.StockService;

import java.text.DecimalFormat;
import java.util.stream.IntStream;

@SpringBootTest
public class InsertDataTest {
    @Autowired
    private PasswordEncoder passwordEncoder; // PasswordEncoder 주입

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private BomRepository bomRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private MaterialRepository materialRepository;

    @Autowired
    private StockService stockService;


    @Test
    public void insertAll() {

        insertProduct();
        insertUser();
        insertUserForUse();
        insertSupplier();
        insertMaterial();
        insertAssyMaterial();
        insertBom();

    }


    @Test
    public void insertUserForUse() {
        User user = User.builder()
                .userId("user1")
                .username("user")
                .userAddress("주소")
                .password(passwordEncoder.encode("1234"))
                .phone("000-0000-0000")
                .userType(UserType.PARTNER)
                .build();
        userRepository.save(user);

        User admin = User.builder()
                .userId("admin")
                .username("admin")
                .userAddress("주소")
                .password(passwordEncoder.encode("1234"))
                .phone("000-0000-0000")
                .userType(UserType.ADMIN)
                .build();
        userRepository.save(admin);

        Supplier supplier = Supplier.builder()
                .user(admin)
                .supplierId(admin.getUserId())
                .businessRegistrationNumber("000-00-00000")
                .supplierName("DG전동")
                .contactInfo("000-0000-0000")
                .address("경기도 수원시")
                .businessType("우리 회사")
                .businessItem("우리 회사")
                .approved(true)
                .build();
        supplierRepository.save(supplier);

    }


    //상품 등록
    @Test
    public void insertProduct() {

        Product product1 = Product.builder()
                .productCode("MATB3FIN001")
                .productName("전기자전거A")
                .build();
        productRepository.save(product1);


        Product product2 = Product.builder()
                .productCode("MATB3FIN002")
                .productName("전기자전거B")
                .build();
        productRepository.save(product2);


        Product product3 = Product.builder()
                .productCode("MATK2FIN001")
                .productName("전동킥보드")
                .build();
        productRepository.save(product3);

    }

    //유저 등록
    @Test
    public void insertUser() {
        String[] supplierName = {"신지전자", "imbot", "시마노", "Oregon", "볼턴", "흥아타이어", "스람", "COS", "훔페르트",
                "JQORG", "Ergotec", "BTR", "몬스터라이트", "발켄", "로터바이크", "벨로또", "Ebike", "한국자전거", "TSUNAMI",
                "Deda", "KHS", "e근두운", "TK", "UNO", "LOOBON", "스기노", "KUDOS", "AU테크", "Mankeel", "Tektro", "Kuntai",
                "EVE", "Lankeleisi", "JAK", "Celeron", "Kenda", "Ninebot", "Lingbo"};

        IntStream.rangeClosed(1, 38).forEach(i -> {
            User user = User.builder()
                    .userId("supplier" + i)
                    .username(supplierName[i - 1])
                    .userAddress("주소")
                    .password(passwordEncoder.encode("1234"))
                    .phone("000-0000-0000")
                    .userType(UserType.SUPPLIER)
                    .build();
            userRepository.save(user);
        });
    }

    //업체 등록
    @Test
    public void insertSupplier() {
        String[] supplierName = {"신지전자", "imbot", "시마노", "Oregon", "볼턴", "흥아타이어", "스람", "COS", "훔페르트",
                "JQORG", "Ergotec", "BTR", "몬스터라이트", "발켄", "로터바이크", "벨로또", "Ebike", "한국자전거", "TSUNAMI",
                "Deda", "KHS", "e근두운", "TK", "UNO", "LOOBON", "스기노", "KUDOS", "AU테크", "Mankeel", "Tektro", "Kuntai",
                "EVE", "Lankeleisi", "JAK", "Celeron", "Kenda", "Ninebot", "Lingbo"};

        IntStream.rangeClosed(1, 38).forEach(i -> {

            DecimalFormat df = new DecimalFormat("00000");
            if (userRepository.findByUserId("supplier" + i).isPresent()) {
                User user = userRepository.findByUserId("supplier" + i).get();
                Supplier supplier = Supplier.builder()
                        .user(user)
                        .supplierId(user.getUserId())
                        .businessRegistrationNumber("000-00-" + df.format(i))
                        .supplierName(supplierName[i - 1])
                        .contactInfo("000-0000-0000")
                        .address("경기도 수원시")
                        .businessType("금속제조")
                        .businessItem("사출금형")
                        .approved(true)
                        .build();
                supplierRepository.save(supplier);

            }
        });
    }

    //자재 등록
    @Test
    public void insertMaterial() {
        String[] componentTypes = {"경적", "경적", "경적+전조등", "계기판+스로틀 레버", "계기판+스로틀 레버", "계기판+스로틀 레버",
                "기둥 프레임", "로터", "로터", "로터", "림", "림", "메인 케이블", "바텀브라켓 셋", "바텀브라켓 셋", "발판", "배터리",
                "배터리", "배터리", "밸브", "밸브", "변속기(뒤)", "변속기(뒤)", "변속기(앞)", "변속기(앞)", "브레이크 레버  우",
                "브레이크 레버  우", "브레이크 레버  좌", "브레이크 레버  좌", "브레이크 케이블", "브레이크 케이블", "스탠드", "스탠드",
                "스탠드", "스포크", "스포크", "스프라켓", "스프라켓", "시트 클램프", "시트 클램프", "시트 포스트", "시트 포스트",
                "안장 날개", "안장 날개", "전조등", "전조등", "체인 휠세트", "체인 휠세트", "캘리퍼", "캘리퍼", "캘리퍼", "컨트롤러",
                "컨트롤러", "컨트롤러", "크랭크", "크랭크", "타이어", "타이어", "타이어", "튜브", "튜브", "페달", "페달",
                "페달 보조 센서", "포크", "포크", "프레임", "핸들 그립", "핸들 그립", "핸들바", "핸들바", "핸들바", "허브", "허브",
                "허브 모터", "허브 모터", "허브 모터", "헤드셋", "헤드셋", "휠"};

        String[] materialCodes = {"MATB3MAT001", "MATB3MAT002", "MATK2MAT001", "MATB2MAT001", "MATB2MAT002", "MATHAMAT001",
                "MATBOMAT001", "MATWHMAT001", "MATWHMAT002", "MATWHMAT003", "MATRIMAT001", "MATRIMAT002", "MATK1MAT001",
                "MATB1MAT001", "MATB1MAT002", "MATBOMAT002", "MATB2MAT003", "MATB2MAT004", "MATK1MAT002", "MATWHMAT004",
                "MATWHMAT005", "MATB1MAT003", "MATB1MAT004", "MATB1MAT005", "MATB1MAT006", "MATHAMAT002", "MATHAMAT003",
                "MATHAMAT004", "MATHAMAT005", "MATB1MAT007", "MATB1MAT008", "MATB1MAT009", "MATB1MAT010", "MATK1MAT003",
                "MATRIMAT003", "MATRIMAT004", "MATWHMAT006", "MATWHMAT007", "MATB1MAT011", "MATB1MAT012", "MATSAMAT001",
                "MATSAMAT002", "MATSAMAT003", "MATSAMAT004", "MATB3MAT003", "MATB3MAT004", "MATB1MAT013", "MATB1MAT014",
                "MATB1MAT015", "MATB1MAT016", "MATK1MAT004", "MATB2MAT005", "MATB2MAT006", "MATK1MAT005", "MATB1MAT017",
                "MATB1MAT018", "MATWHMAT008", "MATWHMAT009", "MATWHMAT010", "MATWHMAT011", "MATWHMAT012", "MATPEMAT001",
                "MATPEMAT002", "MATPEMAT003", "MATB1MAT019", "MATB1MAT020", "MATB1MAT021", "MATHAMAT006", "MATHAMAT007",
                "MATHAMAT008", "MATHAMAT009", "MATHAMAT010", "MATRIMAT005", "MATRIMAT006", "MATWHMAT013", "MATWHMAT014",
                "MATWHMAT015", "MATB1MAT022", "MATB1MAT023", "MATWHMAT016"};

        String[] materialNames = {"전자식 경적", "수동식 경적", "6구 12W 라이트 + 경적", "계기판 스로틀 디스플레이 타입A",
                "계기판 스로틀 디스플레이 타입B", "AU테크 레드윙 전동킥보드 전용 계기판/속도계 일반형", "8인치 전면 기둥 프레임",
                "시마노SM-RT66 로터", "시마노SM-RT56 로터", "140mm 로터", "알루미늄 더블림 29인치", "알루미늄 싱글림 29인치",
                "6선 케이블 1.8m", "시마노SM-BB52 바텀브라켓", "시마노E25 옥타링크 바텀브라켓", "8인치 킥보드 쇼바 튜닝용 발판",
                "48V 리튬이온 배터리 - 볼턴 배터리", "e근두운 36v 리튬이온 배터리", "36V 15AH 배터리(기본형)", "프레스타 밸브 60mm",
                "프레스타 밸브 48mm", "스람 x5 10단 뒷변속기", "스람 x4 (7,8,9단)", "시마노RD-M4120 10단", "시마노FD-M4000 9단",
                "시마노BL-MT201 브레이크 레버 (우)", "시마노BL-MT200 브레이크 레버 (우)", "시마노BL-MT201 브레이크 레버 (좌)",
                "시마노BL-MT200 브레이크 레버 (좌)", "COS 스테인레스 블랙 데프콘코팅 브레이크속선", "시마노슬릭 스테인레스 브레이크속선",
                "킥스탠드", "센터 스탠드", "롬 킥 스탠드", "스테인리스 스포크 2.0mm", "스테인리스 스포크 1.8mm",
                "시마노10단 디오레티아그라 스프라켓", "시마노9단 카세트 스프라켓", "시마노프로 QR 시트클램프", "레인보우 QR 시트클램프",
                "서스펜션 시트포스트 pm-705", "sp242 시트포스트[레일식]", "BTR 무통증 스프링 쿠션 자전거", "LOOBON 고탄성 스프링 쿠션",
                "LED 전조등", "할로겐 전조등", "시마노FC-M6000 데오레 10단 체인휠세트", "시마노FC-M371 체인휠세트 9단",
                "시마노DB-MT201 유압식 디스크 브레이크 (좌우 세트)", "시마노BR-MT410 유압식 브레이크 캘리퍼", "JAK9 브레이크 캘리퍼",
                "48V 800W 컨트롤러", "36V 500W 컨트롤러", "36v 1600w BLDC 모터 컨트롤러", "로터바이크 인파워 DM 파워미터 크랭크세트",
                "스기노 RD2 PJ130 메신져 트랙 픽시 크랭크세트 46T", "벨로또 세띠아 디펜더 (29x1.75)", "벨로또 세띠아 디펜더(29 x 1.75)",
                "8인치 200×50 통타이어 3번", "슈발베 29인치[27.5겸용] 프리라이드 광폭 튜브 (40mm) 폭 2.1~3.0",
                "벨로또 29인치 1.5~1.75 프레스타 튜브 40mm", "시마노XT PD-M8100 페달 (XC용, SPD 클릿 포함)", "시마노PD-M520L 페달",
                "페달 보조 센서 용 12 마그네틱 듀얼 홀 통합 전원 Ebike", "한국자전거 포크 서스펜션", "MTB프론트 서스펜션 사이클링 앞 쇼바 알루미늄 포크",
                "자전거 프레임셋 TSUN 알루미늄 고정 기어 및 포크 트랙 픽시", "에르고노믹 핸들 그립", "스탠다드 핸들 그립", "접이식 핸들 일자형 660",
                "제로2 RHM V2 드롭바", "훔페르트 컨테스트 핸들바", "시마노HB-M6010 허브", "시마노HB-M6000 허브",
                "모터 휠 전면 후면 브러시리스 기어드 허브 Ebike 변환 키트용 48V 500w", "브러시리스 기어드 허브 모터 방수 9 핀 36V 250W",
                "8인치 허브모터 36v 450w 8×2.00-5", "KHS-PT1860 인터그레이티드 헤드셋", "KUDOS-Q1 세미-인터그레이티드 레드 헤드셋", "8인치 에어바퀴 휠"};

        String[] materialTypes = {"구매품", "구매품", "구매품", "제조품", "제조품", "제조품", "구매품", "구매품", "구매품", "구매품",
                "구매품", "구매품", "구매품", "구매품", "구매품", "구매품", "제조품", "제조품", "제조품", "구매품", "구매품", "구매품",
                "구매품", "구매품", "구매품", "구매품", "구매품", "구매품", "구매품", "구매품", "구매품", "구매품", "구매품", "구매품",
                "구매품", "구매품", "구매품", "구매품", "구매품", "구매품", "구매품", "구매품", "구매품", "구매품", "구매품", "구매품",
                "구매품", "구매품", "구매품", "구매품", "구매품", "제조품", "제조품", "제조품", "구매품", "구매품", "구매품", "구매품",
                "구매품", "구매품", "구매품", "구매품", "구매품", "구매품", "구매품", "구매품", "구매품", "구매품", "구매품", "구매품",
                "구매품", "구매품", "구매품", "구매품", "구매품", "구매품", "구매품", "구매품", "구매품", "구매품"};

        Double[] unitPrices = {10000D, 5000D, 10000D, 35000D, 30000D, 50000D, 70000D, 20000D, 15000D, 12000D, 60000D,
                50000D, 6000D, 25000D, 20000D, 60000D, 590000D, 453420D, 374000D, 5000D, 4000D, 70000D, 50000D, 60000D,
                40000D, 30000D, 25000D, 30000D, 25000D, 10000D, 8000D, 15000D, 12000D, 25000D, 1000D, 800D, 50000D, 45000D,
                22000D, 8000D, 50000D, 45000D, 30000D, 25000D, 20000D, 15000D, 120000D, 100000D, 114000D, 26000D, 17000D,
                35000D, 25000D, 55000D, 1519000D, 337250D, 35000D, 35000D, 14000D, 14400D, 7500D, 139000D, 53000D, 15000D,
                34500D, 26000D, 300000D, 10000D, 5000D, 32000D, 57000D, 35900D, 40000D, 35000D, 250000D, 200000D, 135000D,
                53000D, 51000D, 13000D};

        Integer[] quantities = {50, 50, 50, 60, 60, 60, 15, 40, 40, 40, 30, 30, 60, 30, 30, 20, 30, 30, 30, 50, 50, 30,
                30, 30, 30, 30, 30, 30, 30, 50, 50, 30, 30, 30, 400, 400, 30, 30, 30, 30, 40, 40, 50, 50, 50, 50, 30, 30,
                30, 30, 30, 20, 20, 20, 30, 30, 30, 30, 30, 60, 60, 30, 30, 50, 30, 30, 30, 40, 40, 30, 30, 30, 40, 40,
                30, 30, 30, 30, 30, 30};

        int[] leadtimes = {2, 2, 2, 10, 10, 10, 5, 1, 1, 1, 1, 1, 1, 1, 1, 5, 15, 15, 15, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2,
                1, 1, 1, 1, 2, 1, 1, 1, 1, 1, 1, 2, 2, 1, 1, 2, 2, 4, 4, 4, 4, 2, 10, 10, 10, 1, 1, 2, 2, 3, 2, 2, 2, 2,
                1, 4, 4, 5, 1, 1, 3, 3, 3, 1, 1, 3, 3, 3, 1, 1, 1};

        String[] supplierNames = {"신지전자", "신지전자", "신지전자", "imbot", "imbot", "AU테크", "Mankeel", "시마노", "시마노",
                "Tektro", "Oregon", "Oregon", "Kuntai", "시마노", "시마노", "Mankeel", "볼턴", "e근두운", "EVE", "흥아타이어",
                "흥아타이어", "스람", "스람", "시마노", "시마노", "시마노", "시마노", "시마노", "시마노", "COS", "시마노", "훔페르트",
                "훔페르트", "Lankeleisi", "JQORG", "JQORG", "시마노", "시마노", "시마노", "TK", "Ergotec", "UNO", "BTR",
                "LOOBON", "몬스터라이트", "몬스터라이트", "시마노", "시마노", "시마노", "시마노", "JAK", "발켄", "발켄", "Celeron",
                "로터바이크", "스기노", "벨로또", "벨로또", "Kenda", "벨로또", "벨로또", "시마노", "시마노", "Ebike", "한국자전거",
                "TSUNAMI", "TSUNAMI", "신지전자", "신지전자", "Ninebot", "Deda", "훔페르트", "시마노", "시마노", "Ebike", "Ebike",
                "Lingbo", "KHS", "KUDOS", "Kenda"};

        IntStream.rangeClosed(1, 80).forEach(i -> {

            String supplierId = supplierRepository.findSupplierIdBySupplierName(supplierNames[i - 1]);
            if (supplierRepository.findById(supplierId).isPresent()) {
                Supplier supplier = supplierRepository.findById(supplierId).get();

                Material material = Material.builder()
                        .materialName(materialNames[i - 1])
                        .materialCode(materialCodes[i - 1])
                        .materialType(materialTypes[i - 1])
                        .componentType(componentTypes[i - 1])
                        .unitPrice(unitPrices[i - 1])
                        .quantity(quantities[i - 1])
                        .leadtime(leadtimes[i - 1])
                        .supplier(supplier)
                        .build();
                materialRepository.save(material);
            }
        });
    }


    //BOM 등록
    @Test
    public void insertBom() {
        String[] productCodes = {"MATB3FIN001", "MATB3FIN001", "MATB3FIN001", "MATB3FIN001", "MATB3FIN001", "MATB3FIN001",
                "MATB3FIN001", "MATB3FIN001", "MATB3FIN001", "MATB3FIN001", "MATB3FIN001", "MATB3FIN001", "MATB3FIN001",
                "MATB3FIN001", "MATB3FIN001", "MATB3FIN001", "MATB3FIN001", "MATB3FIN001", "MATB3FIN001", "MATB3FIN001",
                "MATB3FIN001", "MATB3FIN001", "MATB3FIN001", "MATB3FIN001", "MATB3FIN001", "MATB3FIN001", "MATB3FIN001",
                "MATB3FIN001", "MATB3FIN001", "MATB3FIN001", "MATB3FIN001", "MATB3FIN001", "MATB3FIN001", "MATB3FIN001",
                "MATK2FIN001", "MATK2FIN001", "MATK2FIN001", "MATK2FIN001", "MATK2FIN001", "MATK2FIN001", "MATK2FIN001",
                "MATK2FIN001", "MATK2FIN001", "MATK2FIN001", "MATK2FIN001", "MATK2FIN001", "MATK2FIN001", "MATK2FIN001",
                "MATB3FIN002", "MATB3FIN002", "MATB3FIN002", "MATB3FIN002", "MATB3FIN002", "MATB3FIN002", "MATB3FIN002",
                "MATB3FIN002", "MATB3FIN002", "MATB3FIN002", "MATB3FIN002", "MATB3FIN002", "MATB3FIN002", "MATB3FIN002",
                "MATB3FIN002", "MATB3FIN002", "MATB3FIN002", "MATB3FIN002", "MATB3FIN002", "MATB3FIN002", "MATB3FIN002",
                "MATB3FIN002", "MATB3FIN002", "MATB3FIN002", "MATB3FIN002", "MATB3FIN002", "MATB3FIN002", "MATB3FIN002",
                "MATB3FIN002", "MATB3FIN002", "MATB3FIN002", "MATB3FIN002", "MATB3FIN002", "MATB3FIN002"};

        String[] componentTypes = {"경적", "계기판+스로틀 레버", "로터", "림", "바텀브라켓 셋", "배터리", "밸브", "변속기(뒤)",
                "변속기(앞)", "브레이크 레버 - 우", "브레이크 레버 - 좌", "브레이크 케이블", "스탠드", "스포크", "스프라켓", "시트 클램프",
                "시트 포스트", "안장 날개", "전조등", "체인 휠세트", "캘리퍼", "컨트롤러", "크랭크", "타이어", "튜브", "페달", "페달 보조 센서",
                "포크", "프레임", "핸들 그립", "핸들바", "허브", "허브 모터", "헤드셋", "경적+전조등", "계기판+스로틀 레버", "기둥 프레임",
                "로터", "메인 케이블", "발판", "배터리", "스탠드", "캘리퍼", "컨트롤러", "타이어", "핸들바", "허브 모터", "휠", "경적",
                "계기판+스로틀 레버", "로터", "림", "바텀브라켓 셋", "배터리", "밸브", "변속기(뒤)", "변속기(앞)", "브레이크 레버 - 우",
                "브레이크 레버 - 좌", "브레이크 케이블", "스탠드", "스포크", "스프라켓", "시트 클램프", "시트 포스트", "안장 날개",
                "전조등", "체인 휠세트", "캘리퍼", "컨트롤러", "크랭크", "타이어", "튜브", "페달", "페달 보조 센서", "포크", "프레임",
                "핸들 그립", "핸들바", "허브", "허브 모터", "헤드셋"};

        String[] materialCodes = {"MATB3MAT001", "MATB2MAT001", "MATWHMAT001", "MATRIMAT001", "MATB1MAT001", "MATB2MAT003",
                "MATWHMAT004", "MATB1MAT003", "MATB1MAT005", "MATHAMAT002", "MATHAMAT004", "MATB1MAT007", "MATB1MAT009",
                "MATRIMAT003", "MATWHMAT006", "MATB1MAT011", "MATSAMAT001", "MATSAMAT003", "MATB3MAT003", "MATB1MAT013",
                "MATB1MAT015", "MATB2MAT005", "MATB1MAT017", "MATWHMAT008", "MATWHMAT011", "MATPEMAT001", "MATPEMAT003",
                "MATB1MAT019", "MATB1MAT021", "MATHAMAT006", "MATHAMAT009", "MATRIMAT005", "MATWHMAT013", "MATB1MAT022",
                "MATK2MAT001", "MATHAMAT001", "MATBOMAT001", "MATWHMAT003", "MATK1MAT001", "MATBOMAT002", "MATK1MAT002",
                "MATK1MAT003", "MATK1MAT004", "MATK1MAT005", "MATWHMAT010", "MATHAMAT008", "MATWHMAT015", "MATWHMAT016",
                "MATB3MAT002", "MATB2MAT002", "MATWHMAT002", "MATRIMAT002", "MATB1MAT002", "MATB2MAT004", "MATWHMAT005",
                "MATB1MAT004", "MATB1MAT006", "MATHAMAT003", "MATHAMAT005", "MATB1MAT008", "MATB1MAT010", "MATRIMAT004",
                "MATWHMAT007", "MATB1MAT012", "MATSAMAT002", "MATSAMAT004", "MATB3MAT004", "MATB1MAT014", "MATB1MAT016",
                "MATB2MAT006", "MATB1MAT018", "MATWHMAT009", "MATWHMAT012", "MATPEMAT002", "MATPEMAT003", "MATB1MAT020",
                "MATB1MAT021", "MATHAMAT007", "MATHAMAT010", "MATRIMAT006", "MATWHMAT014", "MATB1MAT023"};

        int[] requireQuantities = {1, 1, 2, 2, 1, 1, 2, 1, 1, 1, 1, 1, 1, 60, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 1,
                1, 1, 1, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 2, 1, 1, 2, 1, 1, 1, 1, 1, 1, 60,
                1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 1, 1, 1, 1, 2, 1, 1};

        IntStream.rangeClosed(1, 82).forEach(i -> {
            Product product = productRepository.findByProductCode(productCodes[i - 1]);

            if (materialRepository.findByMaterialCode(materialCodes[i - 1]).isPresent()) {
                Material material = materialRepository.findByMaterialCode(materialCodes[i - 1]).get();

                Bom bom = Bom.builder()
                        .product(product)
                        .material(material)
                        .componentType(componentTypes[i - 1])
                        .requireQuantity(requireQuantities[i - 1])
                        .build();
                bomRepository.save(bom);
            }
        });
    }

    //조립품 및 완제품 등록
    @Test
    public void insertAssyMaterial() {
        String[] componentTypes = {"완성 림F", "완성 림R", "앞바퀴", "뒷바퀴", "핸들", "조립된 페달", "안장", "자전거1", "자전거2",
                "자전거3", "앞바퀴", "뒷바퀴", "핸들", "킥보드 바디", "킥보드1", "킥보드2", "완성 림F", "완성 림R", "앞바퀴", "뒷바퀴",
                "핸들", "조립된 페달", "안장", "자전거1", "자전거2", "자전거3"};

        String[] materialCodes = {"MATWHSEM001", "MATWHSEM002", "MATB1SEM001", "MATB1SEM002", "MATB1SEM003", "MATB1SEM004",
                "MATB1SEM005", "MATB2SEM001", "MATB3SEM001", "MATB3FIN001", "MATK1SEM001", "MATK1SEM002", "MATK1SEM003",
                "MATK1SEM004", "MATK2SEM001", "MATK2FIN001", "MATWHSEM003", "MATWHSEM004", "MATB1SEM006", "MATB1SEM007",
                "MATB1SEM008", "MATB1SEM009", "MATB1SEM010", "MATB2SEM002", "MATB3SEM002", "MATB3FIN002"};

        String[] materialNames = {"완성 림AF", "완성 림AR", "앞바퀴A", "뒷바퀴A", "핸들A", "조립된 페달A", "안장A", "자전거A1",
                "자전거A2", "자전거A3", "앞바퀴K", "뒷바퀴K", "핸들K", "킥보드 바디K", "킥보드K1", "킥보드K2", "완성 림BF",
                "완성 림BR", "앞바퀴B", "뒷바퀴B", "핸들B", "조립된 페달B", "안장B", "자전거B1", "자전거B2", "자전거B3"};


        Double[] unitPrices = {128000D, 132000D, 202400D, 422500D, 127000D, 154000D, 80000D, 3566300D, 4226300D, 4256300D,
                27000D, 147000D, 82000D, 130000D, 863000D, 873000D, 107400D, 110600D, 168900D, 417100D, 90900D, 68000D,
                70000D, 1861150D, 2369570D, 2389570D};

        IntStream.rangeClosed(1, 26).forEach(i -> {

            String supplierId = supplierRepository.findSupplierIdBySupplierName("DG전동");
            if (supplierRepository.findById(supplierId).isPresent()) {
                Supplier supplier = supplierRepository.findById(supplierId).get();

                String materialType = "조립품";
                if (materialNames[i - 1].equals("자전거B3") || materialNames[i - 1].equals("자전거A3") || materialNames[i - 1].equals("킥보드K2")) {
                    materialType = "완제품";
                }


                Material material = Material.builder()
                        .materialName(materialNames[i - 1])
                        .materialCode(materialCodes[i - 1])
                        .materialType(materialType)
                        .componentType(componentTypes[i - 1])
                        .unitPrice(unitPrices[i - 1])
                        .quantity(1)
                        .leadtime(1)
                        .supplier(supplier)
                        .build();
                materialRepository.save(material);
            }
        });
    }



    //창고 자재 등록
    @Test
    public void insertStock() {
        String[] materialCodes ={"MATB3MAT001","MATB3MAT002","MATK2MAT001","MATB2MAT001","MATB2MAT002","MATHAMAT001",
                "MATBOMAT001","MATWHMAT001","MATWHMAT002","MATWHMAT003","MATRIMAT001","MATRIMAT002","MATK1MAT001",
                "MATB1MAT001","MATB1MAT002","MATBOMAT002","MATB2MAT003","MATB2MAT004","MATK1MAT002","MATWHMAT004",
                "MATWHMAT005","MATB1MAT003","MATB1MAT004","MATB1MAT005","MATB1MAT006","MATHAMAT002","MATHAMAT003",
                "MATHAMAT004","MATHAMAT005","MATB1MAT007","MATB1MAT008","MATB1MAT009","MATB1MAT010","MATK1MAT003",
                "MATRIMAT003","MATRIMAT004","MATWHMAT006","MATWHMAT007","MATB1MAT011","MATB1MAT012","MATSAMAT001",
                "MATSAMAT002","MATSAMAT003","MATSAMAT004","MATB3MAT003","MATB3MAT004","MATB1MAT013","MATB1MAT014",
                "MATB1MAT015","MATB1MAT016","MATK1MAT004","MATB2MAT005","MATB2MAT006","MATK1MAT005","MATB1MAT017",
                "MATB1MAT018","MATWHMAT008","MATWHMAT009","MATWHMAT010","MATWHMAT011","MATWHMAT012","MATPEMAT001",
                "MATPEMAT002","MATPEMAT003","MATB1MAT019","MATB1MAT020","MATB1MAT021","MATHAMAT006","MATHAMAT007",
                "MATHAMAT008","MATHAMAT009","MATHAMAT010","MATRIMAT005","MATRIMAT006","MATWHMAT013","MATWHMAT014",
                "MATWHMAT015","MATB1MAT022","MATB1MAT023","MATWHSEM001","MATWHSEM002","MATB1SEM001","MATB1SEM002",
                "MATB1SEM003","MATB1SEM004","MATB1SEM005","MATB2SEM001","MATB3SEM001","MATB3FIN001","MATK1SEM001",
                "MATK1SEM002","MATK1SEM003","MATK1SEM004","MATK2SEM001","MATK2FIN001","MATWHSEM003","MATWHSEM004",
                "MATB1SEM006","MATB1SEM007","MATB1SEM008","MATB1SEM009","MATB1SEM010","MATB2SEM002","MATB3SEM002",
                "MATB3FIN002"};

        IntStream.rangeClosed(1, 105).forEach(i -> {
            StockDTO stockDTO = StockDTO.builder()
                    .materialCode(materialCodes[i - 1])
                    .quantity(100)
                    .build();
            stockService.register(stockDTO);
        });
    }
}
