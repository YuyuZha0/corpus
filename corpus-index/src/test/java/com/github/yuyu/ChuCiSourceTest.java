package com.github.yuyu;

import com.github.yuyu.entity.ClassicPoetry;
import com.github.yuyu.source.ChuCiSource;
import com.github.yuyu.source.PoetrySource;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

/**
 * @author zhaoyuyu
 * @since 2019/11/20
 */
public class ChuCiSourceTest {

  private List<ClassicPoetry> list;

  @Before
  public void loadList() {
    PoetrySource poetrySource = new ChuCiSource();
    list = poetrySource.get();
  }

  @Test
  public void test() {
    Assert.assertNotNull(list);
    Assert.assertEquals("卷一　离骚", list.get(0).getTitle());
    Assert.assertEquals(
        "陟玉峦兮逍遥，览高冈兮峣峣。<br>"
            + "桂树列兮纷敷，吐紫华兮布条。<br>"
            + "实孔鸾兮所居，今其集兮惟鸮。<br>"
            + "乌鹊惊兮哑哑，余顾兮怊怊。<br>"
            + "彼日月兮暗昧，障覆天兮祲氛。<br>"
            + "伊我后兮不聪，焉陈诚兮效忠。<br>"
            + "摅羽翮兮超俗，游陶遨兮养神。<br>"
            + "乘六蛟兮蜿蝉，遂驰骋兮升云。<br>"
            + "扬彗光兮为旗，秉电策兮为鞭。<br>"
            + "朝晨发兮鄢郢，食时至兮增泉。<br>"
            + "绕曲阿兮北次，造我车兮南端。<br>"
            + "谒玄黄兮纳贽，崇忠贞兮弥坚。<br>"
            + "历九宫兮徧观，睹秘藏兮宝珍。<br>"
            + "就传说兮骑龙，与织女兮合婚。<br>"
            + "举天罼兮掩邪，彀天弧兮射奸。<br>"
            + "随真人兮翱翔，食元气兮长存。<br>"
            + "望太微兮穆穆，睨三阶兮炳分。<br>"
            + "相辅政兮成化，建烈业兮垂勋。<br>"
            + "目瞥瞥兮西没，道遐回兮阻叹。<br>"
            + "志稸积兮未通，怅敞罔兮自怜。<br>"
            + "乱曰：<br>"
            + "天庭明兮云霓藏，三光朗兮镜万方。<br>"
            + "斥蜥蜴兮进龟龙，策谋从兮翼机衡。<br>"
            + "配稷契兮恢唐功，嗟英俊兮未为双。",
        list.get(list.size() - 1).getContent());
  }
}
