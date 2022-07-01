package com.example.formproject;

import com.example.formproject.entity.Crop;
import com.example.formproject.enums.CropCategoryCode;
import com.example.formproject.enums.CropTypeCode;
import com.example.formproject.repository.CropRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class Initialize {
    private CropRepository cropRepository;
    @Autowired
    public Initialize(CropRepository cropRepository){
        this.cropRepository = cropRepository;
    }

    List<Crop> cropsData = Arrays.asList(
            new Crop[]{
                    Crop.builder().category(CropCategoryCode.식량작물.getCategory()).type(CropTypeCode.쌀.getType()).kind("01").name("일반계").build(),
                    Crop.builder().category(CropCategoryCode.식량작물.getCategory()).type(CropTypeCode.쌀.getType()).kind("02").name("백미").build(),
                    Crop.builder().category(CropCategoryCode.식량작물.getCategory()).type(CropTypeCode.쌀.getType()).kind("03").name("현미").build(),
                    Crop.builder().category(CropCategoryCode.식량작물.getCategory()).type(CropTypeCode.쌀.getType()).kind("05").name("햇일반계").build(),
                    Crop.builder().category(CropCategoryCode.식량작물.getCategory()).type(CropTypeCode.찹쌀.getType()).kind("01").name("일반계").build(),
                    Crop.builder().category(CropCategoryCode.식량작물.getCategory()).type(CropTypeCode.콩.getType()).kind("01").name("흰 콩").build(),
                    Crop.builder().category(CropCategoryCode.식량작물.getCategory()).type(CropTypeCode.콩.getType()).kind("02").name("콩나물콩").build(),
                    Crop.builder().category(CropCategoryCode.식량작물.getCategory()).type(CropTypeCode.콩.getType()).kind("03").name("흰 콩(수입)").build(),
                    Crop.builder().category(CropCategoryCode.식량작물.getCategory()).type(CropTypeCode.팥.getType()).kind("00").name("붉은 팥").build(),
                    Crop.builder().category(CropCategoryCode.식량작물.getCategory()).type(CropTypeCode.팥.getType()).kind("01").name("붉은 팥(수입)").build(),
                    Crop.builder().category(CropCategoryCode.식량작물.getCategory()).type(CropTypeCode.녹두.getType()).kind("00").name("녹두").build(),
                    Crop.builder().category(CropCategoryCode.식량작물.getCategory()).type(CropTypeCode.녹두.getType()).kind("01").name("녹두(수입)").build(),
                    Crop.builder().category(CropCategoryCode.식량작물.getCategory()).type(CropTypeCode.메밀.getType()).kind("01").name("메밀(수입)").build(),
                    Crop.builder().category(CropCategoryCode.식량작물.getCategory()).type(CropTypeCode.고구마.getType()).kind("00").name("밤").build(),
                    Crop.builder().category(CropCategoryCode.식량작물.getCategory()).type(CropTypeCode.감자.getType()).kind("01").name("수미").build(),
                    Crop.builder().category(CropCategoryCode.식량작물.getCategory()).type(CropTypeCode.감자.getType()).kind("02").name("대지마").build(),
                    Crop.builder().category(CropCategoryCode.채소류.getCategory()).type(CropTypeCode.배추.getType()).kind("01").name("봄").build(),
                    Crop.builder().category(CropCategoryCode.채소류.getCategory()).type(CropTypeCode.배추.getType()).kind("02").name("고랭지").build(),
                    Crop.builder().category(CropCategoryCode.채소류.getCategory()).type(CropTypeCode.배추.getType()).kind("03").name("가을").build(),
                    Crop.builder().category(CropCategoryCode.채소류.getCategory()).type(CropTypeCode.배추.getType()).kind("06").name("월동").build(),
                    Crop.builder().category(CropCategoryCode.채소류.getCategory()).type(CropTypeCode.양배추.getType()).kind("00").name("양배추").build(),
                    Crop.builder().category(CropCategoryCode.채소류.getCategory()).type(CropTypeCode.시금치.getType()).kind("00").name("시금치").build(),
                    Crop.builder().category(CropCategoryCode.채소류.getCategory()).type(CropTypeCode.상추.getType()).kind("01").name("적").build(),
                    Crop.builder().category(CropCategoryCode.채소류.getCategory()).type(CropTypeCode.상추.getType()).kind("02").name("청").build(),
                    Crop.builder().category(CropCategoryCode.채소류.getCategory()).type(CropTypeCode.얼갈이배추.getType()).kind("00").name("얼갈이배추").build(),
                    Crop.builder().category(CropCategoryCode.채소류.getCategory()).type(CropTypeCode.갓.getType()).kind("00").name("갓").build(),
                    Crop.builder().category(CropCategoryCode.채소류.getCategory()).type(CropTypeCode.수박.getType()).kind("00").name("수박").build(),
                    Crop.builder().category(CropCategoryCode.채소류.getCategory()).type(CropTypeCode.참외.getType()).kind("00").name("참외").build(),
                    Crop.builder().category(CropCategoryCode.채소류.getCategory()).type(CropTypeCode.오이.getType()).kind("01").name("가시계통").build(),
                    Crop.builder().category(CropCategoryCode.채소류.getCategory()).type(CropTypeCode.오이.getType()).kind("02").name("다다기계통").build(),
                    Crop.builder().category(CropCategoryCode.채소류.getCategory()).type(CropTypeCode.오이.getType()).kind("03").name("취청").build(),
                    Crop.builder().category(CropCategoryCode.채소류.getCategory()).type(CropTypeCode.호박.getType()).kind("01").name("애호박").build(),
                    Crop.builder().category(CropCategoryCode.채소류.getCategory()).type(CropTypeCode.호박.getType()).kind("02").name("쥬키니").build(),
                    Crop.builder().category(CropCategoryCode.채소류.getCategory()).type(CropTypeCode.호박.getType()).kind("03").name("단호박").build(),
                    Crop.builder().category(CropCategoryCode.채소류.getCategory()).type(CropTypeCode.토마토.getType()).kind("00").name("토마토").build(),
                    Crop.builder().category(CropCategoryCode.채소류.getCategory()).type(CropTypeCode.딸기.getType()).kind("00").name("딸기").build(),
                    Crop.builder().category(CropCategoryCode.채소류.getCategory()).type(CropTypeCode.무.getType()).kind("01").name("봄").build(),
                    Crop.builder().category(CropCategoryCode.채소류.getCategory()).type(CropTypeCode.무.getType()).kind("02").name("고랭지").build(),
                    Crop.builder().category(CropCategoryCode.채소류.getCategory()).type(CropTypeCode.무.getType()).kind("03").name("가을").build(),
                    Crop.builder().category(CropCategoryCode.채소류.getCategory()).type(CropTypeCode.무.getType()).kind("06").name("월동").build(),
                    Crop.builder().category(CropCategoryCode.채소류.getCategory()).type(CropTypeCode.당근.getType()).kind("00").name("당근").build(),
                    Crop.builder().category(CropCategoryCode.채소류.getCategory()).type(CropTypeCode.당근.getType()).kind("01").name("무세척").build(),
                    Crop.builder().category(CropCategoryCode.채소류.getCategory()).type(CropTypeCode.당근.getType()).kind("02").name("세척 당근").build(),
                    Crop.builder().category(CropCategoryCode.채소류.getCategory()).type(CropTypeCode.당근.getType()).kind("10").name("세척 당근(수입)").build(),
                    Crop.builder().category(CropCategoryCode.채소류.getCategory()).type(CropTypeCode.열무.getType()).kind("00").name("열무").build(),
                    Crop.builder().category(CropCategoryCode.채소류.getCategory()).type(CropTypeCode.건고추.getType()).kind("00").name("화건").build(),
                    Crop.builder().category(CropCategoryCode.채소류.getCategory()).type(CropTypeCode.건고추.getType()).kind("01").name("햇산화건").build(),
                    Crop.builder().category(CropCategoryCode.채소류.getCategory()).type(CropTypeCode.건고추.getType()).kind("02").name("양건").build(),
                    Crop.builder().category(CropCategoryCode.채소류.getCategory()).type(CropTypeCode.건고추.getType()).kind("03").name("햇산양건").build(),
                    Crop.builder().category(CropCategoryCode.채소류.getCategory()).type(CropTypeCode.건고추.getType()).kind("10").name("건고추(수입)").build(),
                    Crop.builder().category(CropCategoryCode.채소류.getCategory()).type(CropTypeCode.풋고추.getType()).kind("00").name("풋고추").build(),
                    Crop.builder().category(CropCategoryCode.채소류.getCategory()).type(CropTypeCode.풋고추.getType()).kind("02").name("꽈리고추").build(),
                    Crop.builder().category(CropCategoryCode.채소류.getCategory()).type(CropTypeCode.풋고추.getType()).kind("03").name("청양고추").build(),
                    Crop.builder().category(CropCategoryCode.채소류.getCategory()).type(CropTypeCode.풋고추.getType()).kind("04").name("오이맛고추").build(),
                    Crop.builder().category(CropCategoryCode.채소류.getCategory()).type(CropTypeCode.붉은고추.getType()).kind("00").name("붉은고추").build(),
                    Crop.builder().category(CropCategoryCode.채소류.getCategory()).type(CropTypeCode.피마늘.getType()).kind("01").name("한지1접").build(),
                    Crop.builder().category(CropCategoryCode.채소류.getCategory()).type(CropTypeCode.피마늘.getType()).kind("02").name("난지1접").build(),
                    Crop.builder().category(CropCategoryCode.채소류.getCategory()).type(CropTypeCode.피마늘.getType()).kind("03").name("한지").build(),
                    Crop.builder().category(CropCategoryCode.채소류.getCategory()).type(CropTypeCode.피마늘.getType()).kind("04").name("난지").build(),
                    Crop.builder().category(CropCategoryCode.채소류.getCategory()).type(CropTypeCode.피마늘.getType()).kind("06").name("햇한지1접").build(),
                    Crop.builder().category(CropCategoryCode.채소류.getCategory()).type(CropTypeCode.피마늘.getType()).kind("07").name("햇난지1접").build(),
                    Crop.builder().category(CropCategoryCode.채소류.getCategory()).type(CropTypeCode.피마늘.getType()).kind("21").name("햇난지(대서)").build(),
                    Crop.builder().category(CropCategoryCode.채소류.getCategory()).type(CropTypeCode.피마늘.getType()).kind("22").name("난지(대서)").build(),
                    Crop.builder().category(CropCategoryCode.채소류.getCategory()).type(CropTypeCode.피마늘.getType()).kind("23").name("햇난지(남도)").build(),
                    Crop.builder().category(CropCategoryCode.채소류.getCategory()).type(CropTypeCode.피마늘.getType()).kind("24").name("난지(남도)").build(),
                    Crop.builder().category(CropCategoryCode.채소류.getCategory()).type(CropTypeCode.양파.getType()).kind("00").name("양파").build(),
                    Crop.builder().category(CropCategoryCode.채소류.getCategory()).type(CropTypeCode.양파.getType()).kind("02").name("햇양파").build(),
                    Crop.builder().category(CropCategoryCode.채소류.getCategory()).type(CropTypeCode.양파.getType()).kind("10").name("수입").build(),
                    Crop.builder().category(CropCategoryCode.채소류.getCategory()).type(CropTypeCode.파.getType()).kind("00").name("대파").build(),
                    Crop.builder().category(CropCategoryCode.채소류.getCategory()).type(CropTypeCode.파.getType()).kind("02").name("쪽파").build(),
                    Crop.builder().category(CropCategoryCode.채소류.getCategory()).type(CropTypeCode.생강.getType()).kind("00").name("생강").build(),
                    Crop.builder().category(CropCategoryCode.채소류.getCategory()).type(CropTypeCode.생강.getType()).kind("01").name("생강(수입)").build(),
                    Crop.builder().category(CropCategoryCode.채소류.getCategory()).type(CropTypeCode.고춧가루.getType()).kind("00").name("고춧가루").build(),
                    Crop.builder().category(CropCategoryCode.채소류.getCategory()).type(CropTypeCode.고춧가루.getType()).kind("01").name("고춧가루(중국)").build(),
                    Crop.builder().category(CropCategoryCode.채소류.getCategory()).type(CropTypeCode.가지.getType()).kind("00").name("가지").build(),
                    Crop.builder().category(CropCategoryCode.채소류.getCategory()).type(CropTypeCode.미나리.getType()).kind("00").name("미나리").build(),
                    Crop.builder().category(CropCategoryCode.채소류.getCategory()).type(CropTypeCode.깻잎.getType()).kind("00").name("깻잎").build(),
                    Crop.builder().category(CropCategoryCode.채소류.getCategory()).type(CropTypeCode.부추.getType()).kind("00").name("부추").build(),
                    Crop.builder().category(CropCategoryCode.채소류.getCategory()).type(CropTypeCode.피망.getType()).kind("00").name("청 피망").build(),
                    Crop.builder().category(CropCategoryCode.채소류.getCategory()).type(CropTypeCode.파프리카.getType()).kind("00").name("파프리카").build(),
                    Crop.builder().category(CropCategoryCode.채소류.getCategory()).type(CropTypeCode.멜론.getType()).kind("00").name("멜론").build(),
                    Crop.builder().category(CropCategoryCode.채소류.getCategory()).type(CropTypeCode.깐마늘_국산.getType()).kind("01").name("깐마늘").build(),
                    Crop.builder().category(CropCategoryCode.채소류.getCategory()).type(CropTypeCode.깐마늘_국산.getType()).kind("03").name("깐마늘(대서)").build(),
                    Crop.builder().category(CropCategoryCode.채소류.getCategory()).type(CropTypeCode.깐마늘_국산.getType()).kind("04").name("햇깐마늘(대서)").build(),
                    Crop.builder().category(CropCategoryCode.채소류.getCategory()).type(CropTypeCode.깐마늘_국산.getType()).kind("05").name("깐마늘(남도)").build(),
                    Crop.builder().category(CropCategoryCode.채소류.getCategory()).type(CropTypeCode.깐마늘_국산.getType()).kind("06").name("햇깐마늘(남도)").build(),
                    Crop.builder().category(CropCategoryCode.과일류.getCategory()).type(CropTypeCode.사과.getType()).kind("01").name("홍옥").build(),
                    Crop.builder().category(CropCategoryCode.과일류.getCategory()).type(CropTypeCode.사과.getType()).kind("05").name("후지").build(),
                    Crop.builder().category(CropCategoryCode.과일류.getCategory()).type(CropTypeCode.사과.getType()).kind("06").name("쓰가루").build(),
                    Crop.builder().category(CropCategoryCode.과일류.getCategory()).type(CropTypeCode.사과.getType()).kind("07").name("홍로").build(),
                    Crop.builder().category(CropCategoryCode.과일류.getCategory()).type(CropTypeCode.배.getType()).kind("01").name("신고").build(),
                    Crop.builder().category(CropCategoryCode.과일류.getCategory()).type(CropTypeCode.배.getType()).kind("02").name("만삼길").build(),
                    Crop.builder().category(CropCategoryCode.과일류.getCategory()).type(CropTypeCode.배.getType()).kind("03").name("장십랑").build(),
                    Crop.builder().category(CropCategoryCode.과일류.getCategory()).type(CropTypeCode.배.getType()).kind("04").name("원황").build(),
                    Crop.builder().category(CropCategoryCode.과일류.getCategory()).type(CropTypeCode.복숭아.getType()).kind("01").name("백도").build(),
                    Crop.builder().category(CropCategoryCode.과일류.getCategory()).type(CropTypeCode.복숭아.getType()).kind("04").name("창방조생").build(),
                    Crop.builder().category(CropCategoryCode.과일류.getCategory()).type(CropTypeCode.복숭아.getType()).kind("05").name("유명").build(),
                    Crop.builder().category(CropCategoryCode.과일류.getCategory()).type(CropTypeCode.포도.getType()).kind("01").name("캠벨얼리").build(),
                    Crop.builder().category(CropCategoryCode.과일류.getCategory()).type(CropTypeCode.포도.getType()).kind("02").name("거봉").build(),
                    Crop.builder().category(CropCategoryCode.과일류.getCategory()).type(CropTypeCode.포도.getType()).kind("03").name("델라웨어").build(),
                    Crop.builder().category(CropCategoryCode.과일류.getCategory()).type(CropTypeCode.포도.getType()).kind("06").name("MBA").build(),
                    Crop.builder().category(CropCategoryCode.과일류.getCategory()).type(CropTypeCode.포도.getType()).kind("08").name("레드글로브 칠레").build(),
                    Crop.builder().category(CropCategoryCode.과일류.getCategory()).type(CropTypeCode.포도.getType()).kind("09").name("레드글로브 페루").build(),
                    Crop.builder().category(CropCategoryCode.과일류.getCategory()).type(CropTypeCode.포도.getType()).kind("10").name("톰슨 미국").build(),
                    Crop.builder().category(CropCategoryCode.과일류.getCategory()).type(CropTypeCode.포도.getType()).kind("11").name("톰슨 호주").build(),
                    Crop.builder().category(CropCategoryCode.과일류.getCategory()).type(CropTypeCode.포도.getType()).kind("12").name("샤인머스켓").build(),
                    Crop.builder().category(CropCategoryCode.과일류.getCategory()).type(CropTypeCode.감귤.getType()).kind("00").name("감귤").build(),
                    Crop.builder().category(CropCategoryCode.과일류.getCategory()).type(CropTypeCode.감귤.getType()).kind("01").name("노지").build(),
                    Crop.builder().category(CropCategoryCode.과일류.getCategory()).type(CropTypeCode.감귤.getType()).kind("02").name("시설").build(),
                    Crop.builder().category(CropCategoryCode.과일류.getCategory()).type(CropTypeCode.단감.getType()).kind("00").name("단감").build(),
                    Crop.builder().category(CropCategoryCode.과일류.getCategory()).type(CropTypeCode.참다래.getType()).kind("01").name("참다래").build(),
            });
    
    @PostConstruct
    public void initializeData(){
        for(Crop c : cropsData){
            try{
                cropRepository.save(c);
            }catch (Exception e){

            }
        }

    }
}
