package com.github.vspiewak.util

import org.scalatest._

class StringUtilsSpec  extends FlatSpec with Matchers with  OptionValues with Inside with Inspectors {

  "A String utility class" should "should remove hashtags" in {

        StringUtils.onlyWords("ok #word") should be ("ok")

    }

    it should "should remove url" in {

      StringUtils.onlyWords("ok http://google.com") should be ("ok")

    }


}
