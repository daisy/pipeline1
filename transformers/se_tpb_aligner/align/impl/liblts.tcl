#!/bin/sh
# the next line restarts using wish \
exec tclsh8.4 "$0" "$@"

namespace eval lts {}

proc lts::init {language treeFile exceptionFile} {
 variable debug
 variable trace
 set debug 5
 
 variable ltsTree
 variable ltstTree
 variable ltsTreeN
 variable ltstTreeN
 if {$treeFile != ""} {
  source $treeFile
 } else {
  #puts stderr "Warning: no treeFile given, can only handle exceptions!"
 }
 
 if {$exceptionFile != ""} {
  variable lts
  variable ltss
  variable ltsw
  variable ltsr
  variable ltsp
  source $exceptionFile
 }
 
 variable a
 catch {source wordstat.tcl}
 # assuming "lag" more common than "slag"
 set a(lag) 10000
 # assuming "nytto" more common than "nytt"
 set a(nytto) 10000
 # assuming "skap" more common than "kap"
 set a(skap) 10000
 set a(skapet) 10000
 set a(skatten) 10000
 set a(skära) 10000
 set a(göraren) 1
 
 variable lang
 set lang $language
 if {$lang != "se" && $lang != "us" && $lang != "en"} {
  error "Unsupported language: $lang"
 }
 
 if 0 { 
  set lts(se,sagomaskin) [list S A: G O M A SJ I: N]
  set ltss(se,sagomaskin) [list -1 1 7]
  set lts(se,sagomaskinen) [list S A: G O M A SJ I: N E0 N]
  set ltss(se,sagomaskinen) [list -1 1 7]
  set lts(se,sagosaker) [list S A: G O S A: K E0 R]
  set ltss(se,sagosaker) [list -1 1 5]
  # cart ger uttalet VA:R som hamnar som första alternativ
  set lts(se,var) [list V A:]
  set lts(se,en) [list E N]
  # för att ändra ordning på uttal, temporär lösning
  set lts(se,bredvid) [list B R E: V I]
  set lts(se,menade) [list M E: N A D E0]
  set lts(se,menar) [list M E: N A R]
 }
 
 set mulpron {}
 variable mult
 variable multb
 variable funcWords
 
 foreach entry [split $mulpron \n] {
  if {$entry == ""} continue
  regsub -all {hy|\*|-|\}|\{} $entry "" entry
  regexp {(\S+)\s+(\S+)} $entry dummy word tran
  set altrans [splitnorm $tran]
  set altrans [Convert2TPA $altrans]
  if {1&&[info exists lts(se,$word)] && \
	  [string equal $lts(se,$word) $altrans]} {
   #puts remove:$word
  } else {
   if {$altrans == "D"} {
    lappend mult($word) "$altrans"
   } else {
    lappend mult($word) $altrans
   }
   set altransstress [splitnormstress $tran]
   set altransstress [Convert2TPA $altransstress]
   lappend multb($word) $altransstress
  }
  set ltsw(se,$word) 1
 }
 lappend funcWords "allt" "den" "det" "de" "dem" "dess" "du" "en" "er" \
     "genom" "han" "hans" "henne" "hennes" "hon" "honom" "här" "att" "i" \
     "jag" "ni" "när" "sen" "ett" "om" "ha" "ju" "sådan" "vi" "vår" \
     "jag" "med" "någon" "så" "på" "har" "och" "skall" "ska" "till" \
     "aldrig" "alltid" "här" "där" "bara" "kan" "man" "ingen" "som" "av" \
     "vill" "varje" "vet" "vem" "antingen" "desto" "mot" "dels" "annan" \
     "va" "vara" "vad" "var" "vid" "vilja" "vilka" "vilken" "vilket" "är" \
     "både" "deras" "då" "eftersom" "eller" "för" "henne" "hennes" "honom" \
     "innan" "kunde" "kunna" "men" "måste" "någonting" "oss" "sig" "sin" \
     "sina" "sitt" "tills" "utan" "ville" "våra" "vårt" "än" "inte" "väl"
 
 variable spell
 foreach {key value} [list a a2:_ b "b e2:_" c "s e2:_" d "d e2:_" e e2:_ f "e_ f:" g "g e2:_" h "h å2:_" i i2:_ j "j i2:_" k "k å2:_" l "e_ l:" m "e_ m:" n "e_ n:" o o2:_ p "p e2:_" q "k u2:_" r "ä3_ r:" s "e_ s:" t "t e2:_" u u2:_ v "v e2:_" w "d u_ b: ë l v e2:" x "e_ k: s" y y2:_ z "s ä2:_ t a" å å2:_ ä ä2:_ ö ö2:_ ø ö2:_ æ ä2:_ 0 0 1 1 2 2 3 3 4 4 5 5 6 6 7 7 8 8 9 9] {
  set spell($key) $value
 }
 
 variable accent
 foreach {key value} [list DUMMY \
			  A \u0104 a \u0105 A \u0102 a \u0103 \
			  A \uc5 Å \uc0 A \uc4 Ä \uc1 A \uc2 A \uc3 \
			  a \ue5 å \ue0 a \ue4 ä \ue1 a \ue2 a \ue3 \
			  Ä \uc6 ä \ue6 \
			  C \uc7 c \ue7 C \u0106 c \u0107 \
			  C \u010c c \u010d \
			  D \u0110 d \u0111 D \ud0 d \uf0 \
			  E \u011a e \u011b E \u0118 e \u0119 \
			  E \u20ac E \uc8 E \ucb E \uc9 E \uca \
			  e \ue8 e \ueb e \ue9 e \uea \
			  G \u011e g \u011f \
			  I \u0130 I \ucc I \ucf I \ucd I \uce \
			  i \u0131 i \uec i \uef i \ued i \uee \
			  L \u20a4  L \u0141 l \u0142 \
			  N \ud1 n \uf1 n \u0143 n \u0144 \
			  N \u0147  n \u0148 \
			  O \ud6 Ö \ud5 O \u0150 O \ud2 O \ud3 O \ud4 \
			  o \u0151 o \uf2 o \uf6 ö \uf3 o \uf4 o \uf5 \
			  O \ud8 o \uf8 Ö \u0152 ö \u0153 \
			  R \u0158 r \u0159 \
			  S \u015a s \u015b s \udf s \u0160 s \u0161 \
			  S \u015e s \u015f \
			  T \ude t \ufe T \u0162 t \u0163 \
			  U \u016e u \u016f \
			  U \ud9 U \udc U \uda U \udb \
			  u \uf9 u \ufc u \ufa u \ufb \
			  y \uff y \ufd Y \udd \
			  Z \u0179 z \u017a Z \u017b z \u017c \
			  Z \u017d z \u017e \
			  <&< \uab >&> \ubb a \uaa o \uba ! \ua1 ? \ubf DUMMY\
			  \u0101 a \u0103 a \u01CE a \
			  \u0113 e \u0115 e \u011B e \
			  \u0107 c \u010d c \u010C C \
			  \u0144 n \u0148 n \
			  \u014D o \u0153 o \u014F o \u01D2 o \
			  \u0155 r \u0159 r \
			  \u015B s \u015F s \
			   \u0160 S \u0161 s \
			  \u016B u \u016D u \u01D4 u \
			  \u017D Z \
			  \u017E z \
			  \u01D0 i \u012B i \u012D i \
			  \u1EEF u \
			  \ufb00 "ff" \ufb01 "fi" \ufb02 "fl" \
			  \ufb03 "ffi" \ufb04 "ffl" \
			  \ufb05 "st" \ufb04 "st" \
			  \u00ad "" \
			 ] {
  set accent($key) $value
 }
 
 variable syll
 set syll(inCC) [list bd bj bl br dj dr dv fj fl fn fr ft gj gl gn gr kl kn kr kt kv mj mn nj pf pj pl pn pr ps pt sf sk sl sm sn sp st sv tr tm tv vr mm pp ll]
 set syll(inCCC) [list skl skr skv spj spl stj str sfr sch ttr]
 set syll(inCCCC) [list schl schm schn schw]
 
 set syll(finCC) [list ch fs ft gd gm jd jf jk jp js jt kt lb lk lf lg lk lm ln lp ls lt lv mb md mf mj mn mp ms mt nd ng nj ns nt nk gn ps pt rb rd rf rg rk rl rm rn rp rs rt  rv sk sm sp st tm ts vd xt]
 set syll(finCCC) [list fst nch sch nkt tch lsk lst msk mst nsk nst psk pst rft rsk rst ngd]
 set syll(finCCCC) [list ngst rsjt nsch]
 
 variable syll2
 set syll2(inCC) [list]
 set syll2(fiCC) [list]
 
 variable endings
 set endings [list \
		  istiska "i s t i s k a" {"JJ SIN DEF NOM POS" "JJ PLU NOM POS"} \
		  ningarna "n i ng a rn a" "" \
		  iskastes "i s k a s t ë s" "" \
		  igastes "i g a s t ë s" "" \
		  iskaste "i s k a s t ë" "" \
		  iskares "i s k a r ë s" "" \
		  ningens "n i ng ë n s" "" \
		  skornas "s k o rn a s" "" \
		  \
		  alitet "a l i t e2: t" "" \
		  istisk "i s t i s k" "" \
		  ningar "n i ng a r" "" \
		  ningen "n i ng ë n" "" \
		  igaste "i g a s t ë" "" \
		  igares "i g a r ë s" "" \
		  iskare "i s k a r ë" "" \
		  iskast "i s k a s t" "" \
		  skorna "s k o rn a" "" \
		  \
		  arnas "a rn a s" "" \
		  astes "a s t ë s" "" \
		  ernas "ë rn a s" "" \
		  igare "i g a r ë" "" \
		  igast "i g a s t" "" \
		  ingar "i ng a r" "" \
		  iskts "i s k t s" "" \
		  iskas "i s k a s" "" \
		  iskes "i s k ë s" "" \
		  nings "n i ng s" "" \
		  skans "s k a n s" "" \
		  skors "s k o rs" "" \
		  \
		  ande "a n d ë" "" \
		  arna "a rn a" "" \
		  ares "a r ë s" "" \
		  aste "a s t ë" "" \
		  ella "ä l a" "" \
		  ellt "ä l t" "" \
		  ende "ë n d ë" "" \
		  erna "ë rn a" "" \
		  igas "i g a s" "" \
		  iges "i g ë s" "" \
		  igts "i g t s" "" \
		  iska "i s k a" "" \
		  iske "i s k ë" "" \
		  isks "i s k s" "" \
		  iskt "i s k t" "" \
		  itet "i t e2: t" "" \
		  liga "l i g a" "" \
		  lige "l i g e" "" \
		  ligs "l i g s" "" \
		  ligt "l i g t" "" \
		  ndet "n d ë t" "" \
		  ndes "n d ë s" "" \
		  ngar "ng a r" "" \
		  ning "n i ng" "" \
		  rnas "rn a s" "" \
		  skan "s k a n" "" \
		  skor "s k o r" "" \
		  stes "s t ë s" "" \
		  \
		  ade "a d ë" "" \
		  arn "a rn" "" \
		  ast "a s t" "" \
		  are "a r ë" "" \
		  ars "a rs" "" \
		  des "d ë s" "" \
		  den "d ë n" "" \
		  ell "ä l" "" \
		  ens "ë n s" "" \
		  ers "ë rs" "" \
		  ets "ë t s" "" \
		  het "h e2: t" "" \
		  isk "i s k" "" \
		  iga "i g a" "" \
		  ige "i g ë" "" \
		  igs "i g s" "" \
		  igt "i g t" "JJ SIN IND NEU NOM POS" \
		  ing "i ng" "" \
		  lig "l i g" "" \
		  nde "n d ë" "" \
		  nar "n a r" "" \
		  nas "n a s" "" \
		  rna "rn a" "" \
		  res "r ë s" "" \
		  ska "s k a" "" \
		  skt "s k t" "" \
		  ste "s t ë" "" \
		  \
		  ad "a d" "PRT PC SIN IND UTR NOM POS" \
		  as "a s" "" \
		  ar "a r" "" \
		  da "d a" "" \
		  de "d ë" "" \
		  ds "d s" "" \
		  en "ë n" "" \
		  et "ë t" "" \
		  er "ë r" "" \
		  es "ë s" "" \
		  ig "i g" "" \
		  ns "n s" "" \
		  na "n a" "" \
		  re "r ë" "" \
		  rs "rs" "" \
		  st "s t" "" \
		  ts "t s" "" \
		  \
		  a "a" "" \
		  d "d" "" \
		  e "ë" "" \
		  n "n" "" \
		  r "r" "" \
		  s "s" "" \
		  t "t" "" \
		 ]
 variable endingsReversed
 set endingsReversed {}
 foreach {s p t} $endings {
  set endingsReversed [linsert $endingsReversed 0 $s $p $t]
 } 
 variable emap
 set emap(n,NN_UTR_SIN_IND_NOM) NN_UTR_SIN_DEF_NOM
 set emap(en,NN_UTR_SIN_IND_NOM) NN_UTR_SIN_DEF_NOM
 set emap(ens,NN_UTR_SIN_IND_NOM) NN_UTR_SIN_DEF_GEN
 set emap(s,NN_NEU_SIN_IND_NOM) NN_NEU_SIN_IND_GEN
 set emap(s,NN_NEU_SIN_DEF_NOM) NN_NEU_SIN_DEF_GEN
 set emap(et,NN_NEU_SIN_IND_NOM) NN_NEU_SIN_DEF_NOM
 set emap(ets,NN_NEU_SIN_IND_NOM) NN_NEU_SIN_DEF_GEN
 set emap(n,NN_NEU_SIN_IND_NOM) NN_NEU_PLU_IND_NOM
 set emap(na,NN_NEU_SIN_IND_NOM) NN_NEU_PLU_DEF_NOM
 set emap(ns,NN_NEU_SIN_IND_NOM) NN_NEU_PLU_IND_GEN
 set emap(nas,NN_NEU_SIN_IND_NOM) NN_NEU_PLU_DEF_GEN
 set emap(t,NN_NEU_SIN_IND_NOM) NN_NEU_SIN_DEF_NOM
 set emap(ts,NN_NEU_SIN_IND_NOM) NN_NEU_SIN_DEF_GEN
 set emap(en,NN_NEU_SIN_IND_NOM) NN_NEU_PLU_DEF_NOM
 set emap(ens,NN_NEU_SIN_IND_NOM) NN_NEU_PLU_DEF_GEN
 set emap(s,NN_UTR_SIN_IND_NOM) NN_UTR_SIN_IND_GEN
 set emap(s,NN_UTR_SIN_DEF_NOM) NN_UTR_SIN_DEF_GEN
 set emap(s,NN_UTR_PLU_IND_NOM) NN_UTR_PLU_IND_GEN
 set emap(s,NN_UTR_PLU_DEF_NOM) NN_UTR_PLU_DEF_GEN
 set emap(ns,NN_UTR_SIN_IND_NOM) NN_UTR_SIN_DEF_GEN
 set emap(ar,NN_UTR_SIN_IND_NOM) NN_UTR_PLU_IND_NOM
 set emap(ars,NN_UTR_SIN_IND_NOM) NN_UTR_PLU_IND_GEN
 set emap(arna,NN_UTR_SIN_IND_NOM) NN_UTR_PLU_DEF_NOM
 set emap(arnas,NN_UTR_SIN_IND_NOM) NN_UTR_PLU_DEF_GEN
 set emap(et,NN_UTR_SIN_IND_NOM) NN_UTR_SIN_DEF_NOM
 set emap(ets,NN_UTR_SIN_IND_NOM) NN_UTR_SIN_DEF_GEN
 set emap(er,NN_UTR_SIN_IND_NOM) NN_UTR_PLU_IND_NOM
 set emap(er,NN_SIN_IND_NOM) NN_UTR_PLU_IND_NOM
 set emap(ers,NN_UTR_SIN_IND_NOM) NN_UTR_PLU_IND_GEN
 set emap(erna,NN_UTR_SIN_IND_NOM) NN_UTR_PLU_DEF_NOM
 set emap(ernas,NN_UTR_SIN_IND_NOM) NN_UTR_PLU_DEF_GEN
 set emap(na,NN_UTR_SIN_IND_NOM) NN_UTR_PLU_DEF_NOM
 set emap(na,NN_UTR_PLU_IND_NOM) NN_UTR_PLU_DEF_NOM
 set emap(nas,NN_UTR_PLU_IND_NOM) NN_UTR_PLU_DEF_GEN
 set emap(s,NN_SIN_IND_NOM) NN_SIN_IND_GEN
 set emap(itet,NN_UTR_SIN_IND_NOM) NN_UTR_SIN_IND_NOM
 set emap(alitet,NN_UTR_SIN_IND_NOM) NN_UTR_SIN_IND_NOM

 set emap(a,JJ_SIN_IND_UTR_NOM_POS) JJ_SIN_DEF_NOM_POS
 set emap(e,JJ_SIN_IND_UTR_NOM_POS) JJ_SIN_DEF_NOM_POS_MAS
 # denna är rätt ibland men det blir totalt 8000 fler fel
 #set emap(e,JJ_SIN_IND_UTR_NOM_POS) JJ_SIN_DEF_NOM_POS
 set emap(t,JJ_SIN_IND_UTR_NOM_POS) JJ_SIN_IND_NEU_NOM_POS
 set emap(are,JJ_SIN_IND_UTR_NOM_POS) JJ_NOM_KOM
 set emap(ast,JJ_SIN_IND_UTR_NOM_POS) JJ_IND_NOM_SUV
 set emap(aste,JJ_SIN_IND_UTR_NOM_POS) JJ_DEF_NOM_SUV
 set emap(s,JJ_SIN_IND_UTR_NOM_POS) JJ_SIN_IND_UTR_GEN_POS
 set emap(s,JJ_SIN_IND_NEU_NOM_POS) JJ_SIN_IND_NEU_GEN_POS
 set emap(as,JJ_SIN_IND_UTR_NOM_POS) JJ_SIN_DEF_GEN_POS
 set emap(es,JJ_SIN_IND_UTR_NOM_POS) JJ_SIN_DEF_GEN_POS_MAS
 set emap(ts,JJ_SIN_IND_UTR_NOM_POS) JJ_SIN_IND_NEU_GEN_POS
 set emap(ares,JJ_SIN_IND_UTR_NOM_POS) JJ_GEN_KOM
 set emap(astes,JJ_SIN_IND_UTR_NOM_POS) JJ_DEF_GEN_SUV
 set emap(isk,NN_UTR_SIN_IND_NOM) JJ_SIN_IND_UTR_NOM_POS
 set emap(iska,NN_UTR_SIN_IND_NOM) JJ_SIN_DEF_NOM_POS
 set emap(iskt,NN_UTR_SIN_IND_NOM) JJ_SIN_IND_NEU_NOM_POS
 set emap(iskts,NN_UTR_SIN_IND_NOM) JJ_SIN_IND_NEU_GEN_POS
 set emap(t,NN_UTR_SIN_IND_NOM) JJ_SIN_IND_NEU_NOM_POS
 set emap(ig,NN_UTR_SIN_IND_NOM) JJ_SIN_IND_UTR_NOM_POS
 set emap(ig,NN_NEU_SIN_IND_NOM) JJ_SIN_IND_UTR_NOM_POS
 set emap(s,JJ_NOM) JJ_GEN

 set emap(a,NN_NEU_SIN_IND_NOM) VB_INF_AKT

 set emap(r,VB_INF_AKT) VB_PRS_AKT
 set emap(s,VB_INF_AKT) VB_INF_SFO
 set emap(t,VB_INF_AKT) VB_SUP_AKT
 set emap(t,VB_IMP_AKT) VB_SUP_AKT
 set emap(de,VB_INF_AKT) VB_PRT_AKT
 set emap(des,VB_INF_AKT) VB_PRT_SFO
 set emap(ts,VB_INF_AKT) VB_SUP_SFO
 set emap(nde,VB_INF_AKT) PRS_PC_NOM
 set emap(ndes,VB_INF_AKT) PRS_PC_GEN

 set emap(er,VB_IMP_AKT) VB_PRS_AKT
 set emap(s,VB_IMP_AKT) VB_IMP_SFO

 set emap(d,VB_INF_AKT) PRT_PC_SIN_IND_UTR_NOM_POS
 #set emap(t,VB_INF_AKT) PRT_PC_SIN_IND_NEU_NOM_POS
 #set emap(de,VB_INF_AKT) PRT_PC_SIN_DEF_NOM_POS
 set emap(ds,VB_INF_AKT) PRT_PC_SIN_IND_UTR_GEN_POS
 #set emap(ts,VB_INF_AKT) PRT_PC_SIN_IND_NEU_GEN_POS
 #set emap(des,VB_INF_AKT) PRT_PC_SIN_DEF_GEN_POS

 set emap(het,JJ_SIN_IND_UTR_NOM_POS) NN_UTR_SIN_IND_NOM

 variable compound
 set compound 0
}

proc lts::splitnorm {str} {
 # special för T*J,S*J alltjämt ALT*JÄMT
 regsub -all {T\*J} $str "ThyJ" str
 regsub -all {S\*J} $str "ShyJ" str
 regsub -all {\*|\"|´|'|`|-|\+|\\|\{|\}} $str "" str
 regsub -all {hy} $str " hy" str
 # special R-SJ R-TJ för t ex energi E N E eps R SJ
 regsub -all {SJ|TJ} $str { &} str
 regsub -all {RS|RT|RL|RN|RD|TJ|SJ|NG|Ö3|Ö4|Ä3|Ä4|A:|I:|E:|O:|U:|Y:|Å:|Ä:|Ö:|A0|E0|F|V|S|H|M|N|R|L|J|E|A|I|O|U|Y|Å|Ä|Ö|P|B|T|D|K|G} $str { &} str
 regsub -all {,|\.|!|\?} $str pau str
 regsub -all " +" $str " " str
 string trim $str
}

proc lts::splitnormstress {str} {
 # special för T*J,S*J alltjämt ALT*JÄMT
 regsub -all {T\*J} $str "ThyJ" str
 regsub -all {S\*J} $str "ShyJ" str
 regsub -all {hy|w|s|a|va|\#|\*|-|\+} $str "" str
 # special R-SJ R-TJ för t ex energi E N E eps R SJ
 regsub -all {SJ|TJ} $str { &} str
 regsub -all {'} $str  " _1 " str
 regsub -all {\"} $str " _2 " str
 regsub -all {`} $str  " _3 " str
 regsub -all {RS|RT|RL|RN|RD|TJ|SJ|NG|Ö3|Ö4|Ä3|Ä4|A:|I:|E:|O:|U:|Y:|Å:|Ä:|Ö:|A0|E0|F|V|S|H|M|N|R|L|J|E|A|I|O|U|Y|Å|Ä|Ö|P|B|T|D|K|G} $str { &} str
 regsub -all {,|\.|!|\?} $str pau str
 regsub -all " +" $str " " str
 regsub -all {(_\d)\s+(\S+)} $str {\2\1} str
 string trim $str
}


proc lts::walkTree {dt} {
 variable featVect
 if {[llength $dt] > 3} {
  return $dt
 }
 switch -- [llength $dt] {
  0 -
  1 -
  2 {return $dt}
  3 {
   set match 0
   set question     [lindex $dt 0]
   set context      [lindex $question 0]
   set pattern      [lindex $question 1]
   if {[regexp $pattern [lindex $featVect $context]]} {
    set match 1
   }
   #if {[info exists ::treeDebug]} { puts $question,$pattern,$context:$match }

   if {$match} {
    walkTree [lindex $dt 1]
   } else {
    walkTree [lindex $dt 2]
   }
  }
  default {error "bad dtree $dt"}
 }
}

proc lts::addStressMarkers {trans stress} {
 #puts [info level 0]
 variable lang
 
 # Panic fix for the case where no main stress was assigned
 if {[lindex $stress 0] == -1 && [lindex $stress 1] == -1} {
  set stress [list -1 0 [lindex $stress 2]]
 }
 # Can not have both accent I and accent II
 if {$stress != "" && [lindex $stress 0] != -1 && [lindex $stress 1] != -1} {
  #set stress [list [lindex $stress 0] -1 [lindex $stress 2]]
  set stress [list [lindex $stress 0] -1 -1]
 }
 # Can not have both accent I and secondary stress
 if {$stress != "" && [lindex $stress 0] != -1 && [lindex $stress 2] != -1} {
  set stress [list [lindex $stress 0] -1 -1]
 }

 set strdebug 0

 if {$stress != ""} {
  for {set i 0} {$i < 3} {incr i} {
   if $strdebug { puts "Initial stress ($i): $stress Trans: $trans" }
   set pos [lindex $stress $i]
   if {-1 != $pos} {
    if {$pos >= [llength $trans]} {
     set pos 0
    }
    # fix 050922 för att rätta upp betoningsfel, listan ej komplett?
    if {[regexp -nocase {B|C|D|F|G|H|J|K|L|M|^N|P|Q|R|S|T|V|X|Z|\-|\_} [lindex $trans $pos]]} {
     if {$i == 2} {
      set pos [expr [llength $trans]-1]
     } else {
      set pos 0
     }
    }
 
    if $strdebug { puts "After length check ($i): $pos Stress: $stress" }
    if {$i < 2} {
     while {[regexp -nocase {B|C|D|F|G|H|J|K|L|M|^N|P|Q|R|S|T|V|X|Z|\-|\_} \
		 [lindex $trans $pos]]} { incr pos }
    } else {
     while {[regexp -nocase {B|C|D|F|G|H|J|K|L|M|^N|P|Q|R|S|T|V|X|Z|\-|\_} \
		 [lindex $trans $pos]]} { incr pos -1}
    }
    if $strdebug { puts "After assert vowel $pos" }
    # Make sure only one stress marker gets assigned to a vowel
    if {$i == 1 && $pos == [lindex $stress 0]} continue
    if {$i == 2 && $pos == [lindex $stress 0]} continue
    if {$i == 2 && $pos == [lindex $stress 1]} continue
    if $strdebug { puts "After multiple check: $pos" }
    if {$pos >= [llength $trans]} {
     set pos 0
     if {$i == 2} continue
    }
    if {$i == 2 && $pos == [lindex $stress 1]} continue
    if $strdebug { puts "After final checks ($i)" }
    if {$lang == "us"} {
     set stressVal $i
    } else {
     set stressVal [expr $i+1]
    }
    set trans [lreplace $trans $pos $pos [lindex $trans $pos]_$stressVal]
    set stress [lreplace $stress $i $i $pos]
   }
  }
 }
 return $trans
}

# This should not be used outside the library, cannot handle xxx-xxx words
proc lts::stress {domain word} {
 #puts stderr [info level 0]
 variable featVect
 variable ltstTree
 variable ltss
 variable compound
 
 set word [string tolower $word]

 if {$compound == 1} {
  if {$word == "alkohol"} { return [list [list 5 -1 -1]] }
  if {$word == "internet"} { return [list [list 5 -1 -1]] }
 }

 if {[info exists ltss($domain,$word)]} {
  #  puts "lts::stress ltss($word): $ltss($domain,$word)"
  return $ltss($domain,$word)
 }
 if {[string match *s $word] && \
	 [info exists ltss($domain,[string range $word 0 end-1])]} {
  #  puts "lts::stress ltss($word): $ltss($domain,$word)"
  return $ltss($domain,[string range $word 0 end-1])
 }
 if {[info exists ltss($domain,${word}e)]} {
  #  puts "lts::stress ltss($word): $ltss($domain,${word}e)"
  return $ltss($domain,${word}e)
 }


 set firstCh 10
 set lastCh  10
 set firstVo 5
 set lastVo  5
 set vector {}
 for {set i 0} {$i < $firstCh} {incr i} {
  lappend vector [string index $word $i]
 }
 for {set i $lastCh} {$i > 0} {incr i -1} {
   lappend vector [string index $word [expr [string length $word] - $i]]
 }
 set nChars [string length $word]
 regsub -all {b|c|d|f|g|h|j|k|l|m|n|p|q|r|s|t|v|x|z} $word "" word
 for {set i 0} {$i < $firstVo} {incr i} {
  lappend vector [string index $word $i]
 }
 for {set i $lastVo} {$i > 0} {incr i -1} {
  lappend vector [string index $word [expr [string length $word] - $i]]
 }
 set nVowels [string length $word]
 lappend vector $nChars $nVowels
 set featVect $vector
 set stress1 [walkTree $ltstTree($domain,1)]
 set stress2 [walkTree $ltstTree($domain,2)]
 set stress3 [walkTree $ltstTree($domain,3)]
 set stressPositions [list $stress1 $stress2 $stress3]
 #puts "lts::stress return $stressPositions"
 return [list $stressPositions]
}

proc lts::stressName {word} {
 variable featVect
 variable ltstTreeN
 variable ltss
 variable lang
 
 set word [string tolower $word]
 if {[info exists ltss($lang,$word)]} {
  #  puts "lts::stress ltss($word): $ltss($lang,$word)"
  return $ltss($lang,$word)
 }

 set firstCh 10
 set lastCh  10
 set firstVo 5
 set lastVo  5
 set vector {}
 for {set i 0} {$i < $firstCh} {incr i} {
  lappend vector [string index $word $i]
 }
 for {set i $lastCh} {$i > 0} {incr i -1} {
   lappend vector [string index $word [expr [string length $word] - $i]]
 }
 set nChars [string length $word]
 regsub -all {b|c|d|f|g|h|j|k|l|m|n|p|q|r|s|t|v|x|z} $word "" word
 for {set i 0} {$i < $firstVo} {incr i} {
  lappend vector [string index $word $i]
 }
 for {set i $lastVo} {$i > 0} {incr i -1} {
  lappend vector [string index $word [expr [string length $word] - $i]]
 }
 set nVowels [string length $word]
 lappend vector $nChars $nVowels
 set featVect $vector
 set stressPositions [walkTree $ltstTreeN($lang,1)]
 lappend stressPositions [walkTree $ltstTreeN($lang,2)]
 lappend stressPositions [walkTree $ltstTreeN($lang,3)]
 #puts "lts::stressName return $stressPositions"
 return [list $stressPositions]
}

if 0 {
proc Convert2TPA {trans} {
 regsub -all {:} $trans "2:" trans
 regsub -all {Ä3} $trans "ä3:" trans
 regsub -all {Ä4} $trans "ä3"  trans
 regsub -all {Ö3} $trans "ö3:" trans
 regsub -all {Ö4} $trans "ö3"  trans
 regsub -all {E0} $trans "ë"   trans
 set trans [string tolower $trans]

 return $trans
}
}

proc lts::stem {word} {
 variable lang
 variable ltsw
 variable a
 variable ltsp
 variable trace
 variable debug
 variable endingsReversed
 variable emap

 set stemTrace 0
 
  foreach var [list a ltsw] {
   foreach {s l t} $endingsReversed {
    if {$stemTrace} {
    puts "*$s $word [string match *$s $word]"
   }
   
   if {[string match *$s $word]} {
    set slen [string length $s]
    set root [string range $word 0 end-$slen]
    if {$stemTrace} {
     puts $root,[info exists a($lang,$root)],[info exists ltsw($lang,$root)]
    }
    if {[info exists ${var}($lang,$root)]} {
     if {$debug} { append trace "Stemming ($root $s), " }
     set pron [lindex [transcribe [string range $word 0 end-$slen]] 0 0]
     
     # xxN-Nxx -> xx-Nxx
     if {[lindex $pron end] == [lindex $l 0]} {
      set pron [lreplace $pron end end]
     }
     
     if {[lindex $pron end] == "r" && $l == "n s"} {
      set pron [lreplace $pron end end rn rs]
     } elseif {[lindex $pron end] == "r" && $l == "s t"} {
      set pron [lreplace $pron end end rs rt]
     } elseif {[lindex $pron end] == "r" && $l == "t s"} {
      set pron [lreplace $pron end end rt rs]
     } elseif {[lindex $pron end] == "r" && [lindex $l 0] == "t"} {
      set pron [lreplace $pron end end rt]
      set pron [concat $pron [lrange $l 1 end]]
     } elseif {[lindex $pron end] == "r" && [lindex $l 0] == "d"} {
      set pron [lreplace $pron end end rd]
      set pron [concat $pron [lrange $l 1 end]]
     } elseif {[lindex $pron end] == "r" && [lindex $l 0] == "l"} {
      set pron [lreplace $pron end end rl]
      set pron [concat $pron [lrange $l 1 end]]
     } elseif {[lindex $pron end] == "r" && $l == "n s"} {
      set pron [lreplace $pron end end rn rs]
     } elseif {[lindex $pron end] == "r" && [lindex $l 0] == "n"} {
      set pron [lreplace $pron end end rn]
      set pron [concat $pron [lrange $l 1 end]]
     } elseif {[lindex $pron end] == "r" && [lindex $l 0] == "s"} {
      set pron [lreplace $pron end end rs]
      set pron [concat $pron [lrange $l 1 end]]
     } elseif {[lindex $pron end] == "rn" && $l == "s"} {
      set pron [concat $pron rs]
     } elseif {[lindex $pron end] == "rt" && $l == "s"} {
      set pron [concat $pron rs]
     } elseif {[lindex $pron end] == "rd" && $l == "s"} {
      set pron [concat $pron rs]
     } elseif {[lindex $pron end] == "sj" && $l == "n"} {
      set pron [concat $pron ë n] ;#reportage-n
     } elseif {[lindex $pron end] == "sj" && $l == "t"} {
      set pron [concat $pron ë t] ;#reportage-t
     } elseif {[lindex $pron end] == "r" && $l == "s"} {
      puts "never get here";exit
      set pron [lreplace $pron end end rs]
     } else {
      set pron [concat $pron $l]
     }
     if {$stemTrace} {
      puts stderr stem-postag,$root,[info exists ltsp($lang,$root)],[info exists emap($s,[lindex $ltsp($lang,$root) 0])],[info exists emap($s,[lindex $ltsp($lang,$root) 1])],$s,[lindex $ltsp($lang,$root) 0],[lindex $ltsp($lang,$root) 1]
     }
     if {[info exists ltsp($lang,$root)] && [info exists emap($s,[lindex $ltsp($lang,$root) 0])]} {
      if {$stemTrace} {
       puts stderr AAA
      }
      set posTag $emap($s,[lindex $ltsp($lang,$root) 0])
      #set posTag [lindex $ltsp($lang,$root) 0]
     } elseif {[info exists ltsp($lang,$root)] && [info exists emap($s,[lindex $ltsp($lang,$root) 1])]} {
      if {$stemTrace} {
       puts stderr BBB
      }
      set posTag $emap($s,[lindex $ltsp($lang,$root) 1])
      #set posTag [lindex $ltsp($lang,$root) 0]
     } else {
      
      set posTag XXX
     }
     return [list "$root $s" $pron $posTag]
    }
   }
  }
 }
 
 return {{} {} {}}
}

# Divide compound word into items found in lexicon

proc lts::divide {word} {
 #puts [info level 0]
 variable lang
 variable ltsw
 variable a
 set pos -1
 set pos2 -1
 set pos3 -1

 set divideTrace 0
 
 # The obvious case
 if {[string match *-* $word]} {
  return [split $word -]
 }
 
 set oword $word
 set word [string tolower $word]
 
 # o-word, if word exists
 if {[string match o* $word] && \
	 [info exists ltsw($lang,[string range $word 1 end])]} {
  return [list "o" [string range $word 1 end]]
 }
 
 # word-a+ering... verkar detta produktivt xxx
 if {[string match *ering $word] && \
	 [info exists ltsw($lang,[string range $word 0 end-5]a)]} {
  #  return [list [string range $word 0 end-5]a ering]
 }
 
 # word-word and variants
 set score -1
 set bestSplit ""
 for {set i 1} {$i < [string length $word]-2} {incr i} {
  set word1  [string range $word 0 $i]
  set word2  [string range $word [expr $i+1] end]
  set word2s [string range $word [expr $i+2] end]

  if {$divideTrace} {
   puts "Trying1 $word1 $word2 [info exists ltsw($lang,$word1)] [info exists ltsw($lang,$word2)] [info exists a($word2)]"
  }
  # gammal före 070404, varför såg den sån ut?!?!?!?
  # word-word
  if {[info exists ltsw($lang,$word1)] && \
	  ([info exists ltsw($lang,$word2)] || [info exists a($word2)])} {}
  if {[info exists ltsw($lang,$word1)] && [info exists ltsw($lang,$word2)]} {
   if {[info exists a($word1)] == 0} { set a($word1) 0 }
   if {[info exists a($word2)] == 0} { set a($word2) 0 }
   set thisScore [expr $a($word1) * $a($word2)]
   if {$thisScore > $score} {
    set score $thisScore
    if {$divideTrace} {
     puts "a $word1 $word2 $a($word1) $a($word2) $thisScore"
    }
    set bestSplit [list $word1 $word2]
   }
  }
  # xxxnnxxx -> xxxnn-nxxx
  if {[string index $word1 end] == [string index $word2 0]} {
   set missing [string index $word2 0]
   if {[info exists ltsw($lang,$word1)] == 0 && \
	   [info exists ltsw($lang,$word1$missing)] && \
	   [info exists ltsw($lang,$word2)]} { 
    if {[info exists a($word1$missing)] == 0} { set a($word1$missing) 0 }
    if {[info exists a($word2)] == 0} { set a($word2) 0 }
    set thisScore [expr $a($word1$missing) * $a($word2)]
    if {$thisScore > $score} {
     set score $thisScore
     if {$divideTrace} {
      puts "a $word1 $word2 $a($word1$missing) $a($word2)"
     }
     set bestSplit [list $word1$missing $word2]
    }
   }
  }
  # word-s-word
  if {1&&[info exists ltsw($lang,$word1)] && \
	  [string index $word $i] != "s" && \
	  [string index $word [expr $i+1]] == "s" && \
	  [info exists ltsw($lang,$word2s)]} {
   if {[info exists a($word1)] == 0} { set a($word1) 0 }
   if {[info exists a($word2s)] == 0} { set a($word2s) 0 }
   set thisScore [expr $a($word1) * $a($word2s)]
   if {$thisScore > $score} {
    set score $thisScore
    if {$divideTrace} {
     puts "b $word1 $word2s $a($word1) $a($word2s) $thisScore"
    }
    set bestSplit [list ${word1}s $word2s]
   }
  }
  # word(e)-word tiggar(e)-munk
  if {$divideTrace} {
   puts "Trying2 ${word1}e $word2 [info exists ltsw($lang,${word1}e)] [info exists ltsw($lang,$word2)]"
  }
  if {1&&[info exists ltsw($lang,${word1}e)] && \
	  [info exists ltsw($lang,$word2)]} {
   if {[info exists a(${word1}e)] == 0} { set a(${word1}e) 0 }
   if {[info exists a($word2)] == 0} { set a($word2) 0 }
   set thisScore [expr $a(${word1}e) * $a($word2)]
   if {$thisScore > $score} {
    set score $thisScore
    #puts "b $word1 $word2s $a(${word1}e) $a($word2)"
    set bestSplit [list $word1 $word2]
   }
  }
  if {$divideTrace} {
   puts "Trying3 $word1 $word2 [info exists ltsw($lang,$word1)] [info exists ltsw($lang,$word2)]"
  }
 } ;# word-word

 if {$score >= 0} {
  return $bestSplit
 }

 for {set i 1} {$i < [string length $word]-2} {incr i} {
  set word1  [string range $word 0 $i]
  set word2  [string range $word [expr $i+1] end]

  # name-word
  if {$divideTrace} {
   puts "Trying4 $word1 [info exists ::n($word1)] $word2 [info exists ltsw($lang,$word2)]"
  }
  if {[info exists ::n([string tolower $word1])] && \
	  [info exists ltsw($lang,$word2)]} {
   return [list [string totitle [string range $oword 0 $i]] $word2]
  }
  # name-word,experiment, kommer att behövas senare, för yttermalung mfl
  if {$divideTrace} {
   puts "Trying5 $word1 [info exists ltsw($lang,$word1)] $word2 [info exists ::n($word2)]"
  }
  if {0&&[info exists ::n([string tolower $word2])] && \
	  [info exists ltsw($lang,$word1)]} {
   return [list [string range $oword 0 $i] $word2]
  }
  if {$divideTrace} {
   puts "$oword [string is upper [string index $oword 0]] [info exists ::n($word1)] $word1 [info exists ltsw($lang,$word2)] $word2"
  }
  # clpn name-word, krav på att ::n existerar
  if {[string is upper [string index $oword 0]] && \
	   [info exists ::n($word1)] && \
	   [info exists ltsw($lang,$word2)]} {
   return [list [string range $oword 0 $i] $word2]
  }
 } ;# name-word

 # word/name-s-word
 for {set i 1} {$i < [string length $word]-2} {incr i} {
  if {$divideTrace} {
   puts "Trying6 [string range $word 0 $i] [info exists ltsw($lang,[string range $word [expr $i+2] end])] [info exists ltsw($lang,[string range $word 0 $i])] [info exists ltsw($lang,[string range $word [expr $i+2] end])]"
  }

  # word-s-word
  if {[info exists ltsw($lang,[string range $word 0 $i])] && \
	  [string index $word [expr $i+1]] == "s" && \
	  [info exists ltsw($lang,[string range $word [expr $i+2] end])]} {
   return [list [string range $word 0 $i] "s" [string range $word [expr $i+2] end]]
  }

  # name-s-word

  if {[info exists ::n([string tolower [string range $word 0 $i]])] && \
	  [string index $word [expr $i+1]] == "s" && \
	  [info exists ltsw($lang,[string range $word [expr $i+2] end])]} {
   return [list [string totitle [string range $oword 0 $i]] "s" [string range $word [expr $i+2] end]]
  }
 } ;# word/name-s-word
 
 # Try to lemmatize before doing division

 if 1 {
  variable endings
  variable divideRek
  if {[info exists divideRek] == 0} { set divideRek 0 }
  incr divideRek
  if {$divideRek == 1} {
   foreach {s l t} $endings {
    if {[string match *$s $word]} {
     set slen [string length $s]
     set root [string range $word 0 end-$slen]
     set components [divide $root] ; # call divide recursively
     if {[llength $components] > 1} {
      lappend components $s
      incr divideRek -1
      if {$divideTrace} { puts "Lemmatize-divide $components" }
      return $components
     }
    }
   }
  } else {
   incr divideRek -1
   return $word
  }
  incr divideRek -1
 }
 # word-word-word
 set score -1
 for {set i 1} {$i < [string length $word]-4} {incr i} {
  for {set j [expr $i+2]} {$j < [string length $word]-2} {incr j} {
   set word1 [string range $word 0 $i]
   set word2 [string range $word [expr $i+1] $j]
   set word3 [string range $word [expr $j+1] end]
   if {[info exists ltsw($lang,$word1)] && \
	   [info exists ltsw($lang,$word2)] && \
	   [info exists ltsw($lang,$word3)]} {
   if {[info exists a($word1)] == 0} { set a($word1) 0 }
   if {[info exists a($word2)] == 0} { set a($word2) 0 }
   if {[info exists a($word3)] == 0} { set a($word3) 0 }
    set thisScore [expr double($a($word1)) * $a($word2) * $a($word3)]
    if {$divideTrace} {
     puts "Trying7 $word1 $word2 $word3 $a($word1) $a($word2) $a($word3) $thisScore"
    }
    if {$thisScore > $score} {
     set score $thisScore
     set bestSplit [list $word1 $word2 $word3]
    }
   }
  }
 }
 if {$score > -1} { return $bestSplit }
 # word-word-s-word
 for {set i 1} {$i < [string length $word]-4} {incr i} {
  for {set j [expr $i+2]} {$j < [string length $word]-2} {incr j} {
   if {[info exists ltsw($lang,[string range $word 0 $i])] && \
	   [info exists ltsw($lang,[string range $word [expr $i+1] $j])] && \
	   [string index $word [expr $j+1]] == "s" && \
	   [info exists ltsw($lang,[string range $word [expr $j+2] end])]} {
    
    if {$divideTrace} {
     puts "Trying7b [string range $word 0 $i] [string range $word [expr $i+1] $j] s [string range $word [expr $j+2] end]"
    }
    return [list [string range $word 0 $i] \
		[string range $word [expr $i+1] $j] "s" \
		[string range $word [expr $j+2] end]]
   }
  }
 }

 # word-s-word-word
 for {set i 1} {$i < [string length $word]-4} {incr i} {
  for {set j [expr $i+2]} {$j < [string length $word]-2} {incr j} {
   if {[info exists ltsw($lang,[string range $word 0 $i])] && \
	   [string index $word [expr $i+1]] == "s" && \
	   [info exists ltsw($lang,[string range $word [expr $i+2] $j])] && \
	   [info exists ltsw($lang,[string range $word [expr $j+1] end])]} {
    
    if {$divideTrace} {
     puts "Trying7b [string range $word 0 $i] s [string range $word [expr $i+2] $j] s [string range $word [expr $j+1] end]"
    }
    return [list [string range $word 0 $i] "s" \
		[string range $word [expr $i+2] $j] \
		[string range $word [expr $j+1] end]]
   }
  }
 }

 # word-s-word-s-word
 for {set i 1} {$i < [string length $word]-4} {incr i} {
  for {set j [expr $i+2]} {$j < [string length $word]-2} {incr j} {
   if {[info exists ltsw($lang,[string range $word 0 $i])] && \
	   [string index $word [expr $i+1]] == "s" && \
	   [info exists ltsw($lang,[string range $word [expr $i+2] $j])] && \
	   [string index $word [expr $j+1]] == "s" && \
	   [info exists ltsw($lang,[string range $word [expr $j+2] end])]} {
    
    if {$divideTrace} {
     puts "Trying7c [string range $word 0 $i] s [string range $word [expr $i+2] $j] s [string range $word [expr $j+2] end]"
    }
    return [list [string range $word 0 $i] "s" \
		[string range $word [expr $i+2] $j] "s" \
		[string range $word [expr $j+2] end]]
   }
  }
 }

 # word-word-word-word
 for {set i 1} {$i < [string length $word]-6} {incr i} {
  for {set j [expr $i+2]} {$j < [string length $word]-4} {incr j} {
   for {set k [expr $j+2]} {$k < [string length $word]-2} {incr k} {
    if {[info exists ltsw($lang,[string range $word 0 $i])] && \
	    [info exists ltsw($lang,[string range $word [expr $i+1] $j])] && \
	    [info exists ltsw($lang,[string range $word [expr $j+1] $k])] && \
	    [info exists ltsw($lang,[string range $word [expr $k+1] end])]} {
     if {$divideTrace} {
      puts "Trying8 [string range $word 0 $i] [string range $word [expr $i+1] $j] [string range $word [expr $j+1] $k] [string range $word [expr $k+1] end]"
     }
     return [list [string range $word 0 $i] \
		 [string range $word [expr $i+1] $j] \
		 [string range $word [expr $j+1] $k] \
		 [string range $word [expr $k+1] end]]
    }
   }
  }
 }

 # word-word-word-s-word
 for {set i 1} {$i < [string length $word]-6} {incr i} {
  for {set j [expr $i+2]} {$j < [string length $word]-4} {incr j} {
   for {set k [expr $j+2]} {$k < [string length $word]-2} {incr k} {
    if {[info exists ltsw($lang,[string range $word 0 $i])] && \
	    [info exists ltsw($lang,[string range $word [expr $i+1] $j])] && \
	     [info exists ltsw($lang,[string range $word [expr $j+1] $k])] && \
	    [string index $word [expr $k+1]] == "s" && \
	    [info exists ltsw($lang,[string range $word [expr $k+2] end])]} {
     if {$divideTrace} {
      puts "Trying8b [string range $word 0 $i] [string range $word [expr $i+1] $j] [string range $word [expr $j+1] $k] s [string range $word [expr $k+2] end]"
     }
     return [list [string range $word 0 $i] \
		 [string range $word [expr $i+1] $j] \
		 [string range $word [expr $j+1] $k] "s" \
		 [string range $word [expr $k+2] end]]
    }
   }
  }
 }
 # word-word-s-word-word
 for {set i 1} {$i < [string length $word]-6} {incr i} {
  for {set j [expr $i+2]} {$j < [string length $word]-4} {incr j} {
   for {set k [expr $j+2]} {$k < [string length $word]-2} {incr k} {
    if {[info exists ltsw($lang,[string range $word 0 $i])] && \
	    [info exists ltsw($lang,[string range $word [expr $i+1] $j])] && \
	    [string index $word [expr $j+1]] == "s" && \
	     [info exists ltsw($lang,[string range $word [expr $j+2] $k])] && \
	    [info exists ltsw($lang,[string range $word [expr $k+1] end])]} {
     if {$divideTrace} {
      puts "Trying8b [string range $word 0 $i] [string range $word [expr $i+1] $j] s [string range $word [expr $j+2] $k] [string range $word [expr $k+1] end]"
     }
     return [list [string range $word 0 $i] \
		 [string range $word [expr $i+1] $j] "s" \
		 [string range $word [expr $j+2] $k] \
		 [string range $word [expr $k+1] end]]
    }
   }
  }
 }

 # Try to lemmatize before doing division

 if 1 {
  variable endings
  variable divideRek2
  if {[info exists divideRek2] == 0} { set divideRek2 0 }
  incr divideRek2
  if {$divideRek2 == 1} {
   foreach {s l t} $endings {
    if {[string match *$s $word]} {
     set slen [string length $s]
     set root [string range $word 0 end-$slen]
     set components [divide $root] ; # call divide recursively
     if {[llength $components] > 1} {
      lappend components $s
      incr divideRek2 -1
      if {$divideTrace} { puts "Lemmatize-divide $components" }
      return $components
     }
    }
   }
  } else {
   incr divideRek2 -1
   return $word
  }
  incr divideRek2 -1
 }
 
 # Try CART for unknown component
 set maxLen 0
 for {set i 1} {$i < [string length $word]-2} {incr i} {
  if {$divideTrace} {
   puts "Trying9: [string range $word 0 [expr $i-0]] [string range $word [expr $i+1] end] $maxLen"
  }
  # xxx-word
  # one usable component, at least works for sagomaskin, might need more work
  if {[info exists ltsw($lang,[string range $word [expr $i+1] end])] && [regexp {^[bcdfghjklmnpqrstvwxz]+$} [string range $word 0 $i]] == 0} {
   set pos $i
   if {$divideTrace} {
    puts A,[string length [string range $word [expr $i+1] end]],[string range $word [expr $i+1] end]
   }
   set wlen1 [expr [string length $word] - $pos]
   if {$wlen1 > $maxLen} {
    set maxLen $wlen1
    set res [list [string range $word 0 $pos] \
		 [string range $word [expr $pos+1] end]]
   }
  }

  # word-s-xxx
  if {[string index $word $i] == "s" && \
	  [info exists ltsw($lang,[string range $word 0 [expr $i-1]])]} {
   set pos3 $i
   if {$divideTrace} {
    puts B,[string length [string range $word 0 [expr $i-1]]],[string range $word 0 [expr $i-1]]
   }
   set wlen3 $pos3
   if {$wlen3 > $maxLen} {
    set maxLen $wlen3
    set res [list [string range $word 0 [expr $pos3-1]] "s" \
		 [string range $word [expr $pos3+1] end]]
   }
  }

  # word-xxx
  if {[info exists ltsw($lang,[string range $word 0 [expr $i-0]])]} {
   set pos2 $i
   if {$divideTrace} {
    puts C,[string length [string range $word 0 [expr $i-0]]],[string range $word 0 [expr $i-0]]
   }
   set wlen2 $pos2
   if {$wlen2 > $maxLen} {
    set maxLen $wlen2
    set res [list [string range $word 0 [expr $pos2-0]] \
	      [string range $word [expr $pos2+1] end]]
   }
  }
 } ; # end for
 #puts "Unknown $pos $pos2 $pos3 $wlen1 $wlen2 $wlen3 $maxLen"

 if {$maxLen > 0} {
  if {$divideTrace} { puts "maxLen: $maxLen > 0  $res" }
  return $res 
 }
 
 return $word
}

proc lts::fixKlassymbol {components} {
 variable lang
 variable ltsw

 for {set i 0} {$i < [llength $components]-1} {incr i} {
  set word [lindex $components $i]
  set lastChar [string index [lindex $components $i] end]
  set firstChar [string index [lindex $components [expr $i+1]] 0]


  if {[string index $word end] == $firstChar && \
	  [info exists ltsw($lang,${word}$firstChar)]} {
   #puts "Warning: $components"
  }
  #puts "$word [info exists ltsw($lang,$word)] $lastChar $firstChar [info exists ltsw($lang,${word}$firstChar)] ${word}$firstChar"

  # utantilläsningen, "utantil" finns inte men "utantill" finns, använd!
  if {[info exists ltsw($lang,$word)] == 0 && $lastChar == $firstChar && \
	  [info exists ltsw($lang,${word}$firstChar)]} {
   set components [lreplace $components $i $i "${word}$firstChar"]
   #puts AAAAA,$components
  }

  # klassymbol, behövs speciell hantering eftersom både 
  # "klas" och "klass" finns, välj "klass" genom att lägga till "klas" i listan
  set fixList [list klas remis mas skot ful käl lät mis rät hål]
  if {[lsearch $fixList $word] > -1 && $lastChar == $firstChar && \
	  [info exists ltsw($lang,${word}$firstChar)]} {
   set components [lreplace $components $i $i "${word}$firstChar"]
  }

 }
 return $components
}

proc lts::transLex2Tcl {loword trans lang} {
 variable ltss
 
 # trans is a lex transcription convert to tcl format, set ltss() etc
 regsub -all {'} $trans  " _1 " trans
 regsub -all {\"} $trans " _2 " trans
 regsub -all {`} $trans  " _3 " trans
 regsub -all {(_\d)\s+(\S+)} $trans {\2\1} trans
 set a1 [lsearch $trans *_1]
 set b1 [lsearch $trans *_2]
 set c1 [lsearch $trans *_3]
 regsub -all {_1|_2|_3} $trans {} trans
 regsub -all {  } $trans { } trans ;# probably only cosmetic
 # The following check necessary, but probably not the right solution
 # fix "1||" done 070116
 if {1||[info exists ltss($lang,$loword)] == 0} {
  lappend ltss($lang,$loword) [list $a1 $b1 $c1]
 }
 # possible solution would have been, depends on everything being an exception!
 #if {[llength lts(se,de)] == [llength ltss($lang,$loword)]} 
 
 return $trans
}

proc lts::transcribe {args} {
 #puts [info level 0]
 variable lang
 variable lts
 variable ltss
 variable ltsw
 variable ltsp
 variable mult
 variable multb
 variable spell
 variable accent
 variable debug
 variable trace
 variable compound
 variable fullWord
 variable endings

 if 1 {
  # bra ställe att ta bort ord
  set removel [list fallskärmshopp andelsägare kursval rostrött eliters]
  set removel [list]
  foreach remove $removel {
   catch {unset ltsw(se,$remove)}
   catch {unset ltsw(se,$remove)}
   catch {unset lts(se,$remove)}
  }
 }

 array set a [list \
		  -stress 0 \
		  -multiple 0 \
		  -divide 1 \
		  -domain default \
		 ]
 array set a [lreplace $args end end]
 set doStress   $a(-stress)
 set doMultiple $a(-multiple)
 set doDivide   $a(-divide)
 set useDomain  $a(-domain)

 set word [lindex $args end]
 regsub -all {\(|\)|\#|\.|\,|\?|\!|\;|\"|\:|\\|/} $word "" word
 # test 070626
 #regsub -all {\(|\)|\#|\.|\,|\?|\!|\;|\"|\\|/} $word "" word

 if {[string match lts::transcribe* [info level 0]]} {
  set compound 0
  set fullWord $word
 }

 if {$debug} { 
  if {$compound} { append trace \n }
  append trace "transcribe ($args), "
 }

 set res ""
 set resl {}
 
 if {[regexp -- {-$} $word]} {
  set word [string trimright $word -]
  set compound 1
  if {$word == ""} { return [list [list {}] pos_unused] }
 }
 if {[regexp -- {^-} $word]} {
  set word [string trimleft $word -]
 }
 set loword [string tolower $word]

 # bra ställe att trixa med orduttal
 if {0 && $loword == "detta"} {
  if {$doStress} {
   return [list [list {d e_2 t: a}] pos_unused]
  } else {
   return [list [list {d e t: a}] pos_unused]
  }
 }
 if {1 && $loword == "fidusia"} {
  if {$doStress} {
   return [list [list {f i d u2:_1 s i a}] pos_unused]
  } else {
   return [list [list {f i d u2: s i a}] pos_unused]
  }
 }
 if {1 && $loword == "ph"} {
  if {$doStress} {
   return [list [list {p e2:_2 h å2:}] pos_unused]
  } else {
   return [list [list {p e2: h å2:}] pos_unused]
  }
 }

 if {$doStress} {
  set stressPositions [stress $lang $loword]
 } else {
  set stressPositions ""
 }
 if {[info exists ltsp($lang,$loword)]} {
  set posTag [lindex $ltsp($lang,$loword) 0]
 } else {
  set posTag XXX
 }

 if {$lang == "us"} { return [transUS $loword $stressPositions] }

 # Handle digits
 set tmpl {" "}
 if {[string is digit $word] && $word != ""} {
  if {$debug} { append trace "Digits, " }
  foreach word [number2text $word] {
   set transl [lindex [transcribe -stress $doStress -multiple $doMultiple $word] 0]
   set tmpl2 {}
   foreach e $tmpl {
    foreach e2 $transl {
     lappend tmpl2 [string trim "$e $e2"]
    }
   }
   set tmpl $tmpl2
  }
#  lappend tmpl [addStressMarkers [string trim $res] $stressPositions]
  return [list $tmpl RG]
 }

 # Specialfix för fall som "definerade" "debugga"
 if {$compound == 1} {
  set anyPos [list de "d e" en "e2: n" be "b e3" skall "s k a l:" dom "d o m" min "m i2: n" rest "r e s t"]
  set index [lsearch $anyPos $loword]
  if {$index > -1} {
   incr index
   return [list [list [lindex $anyPos $index]] pos_unused]
  }

  set notTail [list döds "d ö t s" krigs "k r i k s" livs "l i f: s" stats "s t a t s" stads "s t a t s" strids "s t r i t s" skogs "s k o k: s" rikt "r i k t" köks "tj ö k: s" guds "g u t s" drog "d r å2: g" sen "s e2: n" grott "g r å t:" sovjet "s å v j e. t"]
  set index [lsearch $notTail $loword]
  if {$index > -1} {
   if {[string match -nocase [lindex $notTail $index]* $fullWord]} {
    incr index
    return [list [list [lindex $notTail $index]] pos_unused]
   }
  }

  set notHead [list kort "k o rt:"]
  set index [lsearch $notHead $loword]
  if {$index > -1} {
   if {[string match -nocase *[lindex $notHead $index] $fullWord]} {
    incr index
    return [list [list [lindex $notHead $index]] pos_unused]
   }
  }
 }
 
 if 1 {
  if {$compound == 1 && [string match *$word $fullWord]} {
   if {[lsearch $endings $word] > -1} {
    set i [lsearch $endings $word]
    incr i
    return [list [list [lindex $endings $i]] pos_unused]
   }
  }
 }

 if {$compound == 1 && $doMultiple == 0 && \
	 [info exists lts($lang,$loword)] == 0 && \
	 [info exists lts($lang,${loword}e)] } {
  set loword ${loword}e
  set prefixForm 1
 }

 if {[info exists lts($lang,$loword)] == 0} {
  # Remove accents
  if {[regexp {^[abcdefghijklmnopqrstuvwxyzåäöéüæ]+$} $loword] == 0} {
   foreach char [array names accent *] {
    regsub -all $char $loword $accent($char) loword
    regsub -all $char $word $accent($char) word
   }
  }
 }
 if {[regexp {.*æ.*} $loword]} {
  #regsub -all {æ} $loword e loword
 }
 
 # check if the word is among exceptions
 if {[info exists lts($lang,$loword)]} {
  if {$doMultiple} {
   if {$debug} { append trace "Found exception $loword -> [lindex $lts($lang,$loword) 0], " }
   foreach lw $lts($lang,$loword) spv $stressPositions {
    lappend resl [addStressMarkers $lw $spv]
   }

   # Copied from below "Check tpblex names"
   if {[string is upper [string index $word 0]] &&[info exists ::n($loword)]} {
    set trans [transLex2Tcl $loword $::n($loword) $lang]
    if {$doStress} {
     set stressPositions [stress $lang $loword]
    } else {
     set stressPositions ""
    }
    lappend resl [addStressMarkers $trans [lindex $stressPositions 0]]
   }

   return [list $resl ${posTag}] ;# hade ${posTag}1 av nån anledning pss nedan
  } else {
   set trans [lindex $lts($lang,$loword) 0]
   if {[info exists prefixForm]} { set trans [lrange $trans 0 end-1] }
   if {$debug} { append trace "Found exception $loword -> $trans, " }
   lappend resl [addStressMarkers $trans [lindex $stressPositions 0]]
   return [list $resl ${posTag}]
  }
 }
 
 # Check tpblex names 

 if {[string is upper [string index $word 0]] && [info exists ::n($loword)]} {
  # if ::n() exists and namelike use ::n()
  set trans [transLex2Tcl $loword $::n($loword) $lang]
  if {$doStress} {
   set stressPositions [stress $lang $loword]
  } else {
   set stressPositions ""
  }
  lappend resl [addStressMarkers $trans [lindex $stressPositions end]]
  return [list $resl PM_NOM]
 }
 if {[string is upper [string index $word 0]] && [string index $word end] == "s" && \
	 [info exists ::n([string range $loword 0 end-1])] } {
  # if name(s) and ::n(name) exists and namelike use ::n()
  set trans [transLex2Tcl $loword $::n([string range $loword 0 end-1]) $lang]
  lappend trans s
  if {$doStress} {
   set stressPositions [stress $lang $loword]
  } else {
   set stressPositions ""
  }
  lappend resl [addStressMarkers $trans [lindex $stressPositions end]]
  return [list $resl PM_GEN]
 }

 # check if word is an acronym 
 
 if {0 && [isAcronym $word]} {
  if {$debug} { append trace "Acronym, " }
  foreach e [spell $word] {
   if {[string is digit $e]} {
    foreach e2 [lindex [transcribe -stress $doStress -multiple $doMultiple \
			    [number2text $e]] 0] {
     append res " $e2"
    }
   } else {
    set dummy1 {}
    foreach el $res { lappend dummy1 0}
    set dummy2 {}
    foreach el $e { lappend dummy2 0}
    # should result in multiple prons, try the acronym lnk cf nk (nk in lex)
    # copy the way Handle create multiple prons
    #   coart xxx xxx res e dummy1 dummy2
    append res " $e"
   }
  }
  if {[string match *:s $word]} {
   set word [string trimright $word :s]
  }
  # fix 050916
  if {[string length $word] == 1} {
   # Stress first letter in AB
   set pos1 [lsearch $res *_]
   if {$doStress} {
    set stressPositions [list $pos1 -1 -1]
   }
  } elseif {[string length $word] == 2} {
   # Stress first letter in AB
   set pos1 [lsearch $res *_]
   set pos2 [lsearch -start [expr $pos1+1] $res *_]
   if {$doStress} {
    set stressPositions [list -1 $pos1 $pos2]
   }
  } elseif {[string length $word] == 3} {
   if {[string match GF* $word] || [string match LK* $word] || [string match LH* $word] || [string match LC* $word] || [string match FH* $word] || [string match KL* $word] || [string match SV* $word] || [string match SC* $word]|| [string match NB* $word]|| [string match RH* $word]|| [string match TC* $word]} {
    # compound stress last in KLH
    set pos1 [lsearch $res *_]
    regsub -all {(_)([^_]*)$} $res {,\2} res
    regsub -all {_} $res "" res
    regsub -all {,} $res "_" res
    set pos2 [lsearch $res *_]
    if {$doStress} {
     set stressPositions [list -1 $pos1 $pos2]
    }
   } else {
    # stress last letter in TPB
    regsub -all {(_)([^_]*)$} $res {,\2} res
    regsub -all {_} $res "" res
    regsub -all {,} $res "_" res
    set pos1 [lsearch $res *_]
    if {$doStress} {
     set stressPositions [list $pos1 -1 -1]
    }
   }
  } else {
   # stress next to last letter in ADHD
   regsub -all {(_)([^_]*)$} $res {\2} res
   regsub -all {(_)([^_]*)$} $res {,\2} res
   regsub -all {_} $res "" res
   regsub -all {,} $res "_" res
   set pos1 [lsearch $res *_]
   if {$doStress} {
    set stressPositions [list -1 $pos1 -1]
   }
  }
  regsub -all {_} $res "" res
  
  lappend resl [addStressMarkers [string trim $res] $stressPositions]
  return [list $resl AN]
 }
 # fix 070413
 regsub -all {[^[:alnum:]_-]} $word "" word
 regsub -all {[^[:alnum:]_-]} $loword "" loword
 
 # pre 070413
 #regsub -all {\W} $word "" word
 #regsub -all {\W} $loword "" loword
 # verkar fungera lika bra nu ta bort
 # regsub -all {_|\W} $word "" word
 # regsub -all {_|\W} $loword "" loword

 # Handle inflectional suffixes
 if {[info exists ltsw($lang,$loword)] == 0} {
  foreach {stemRes trans posTag} [stem $loword] break
  if {[string match "* *" $stemRes]} {
   if {$doStress} {
    set stressPositions [lindex [stress $lang [lindex $stemRes 0]] 0]
   }

   if {[lsearch {het} [lindex $stemRes end]] > -1} {
    if {$doStress} {
     set len 2
     set stressPositions [lreplace $stressPositions 2 2 [expr [llength $trans]-$len]]
    } else {
     set stressPositions ""
    }
   }
   # specialfix för relation-ella
   if {[lsearch {ella ellt ell} [lindex $stemRes end]] > -1} {
    # o2: n ä l -> o n ä l
    regsub {(.*)2[:\.]?(\s.\sä\sl.*)} $trans {\1\2} trans
    if {$doStress} {
     if {"ell" == [lindex $stemRes end]} {
      set len 2
     } else {
      set len 3
     }
     if {[lindex $stressPositions 2] == -1} {
      set stressPositions [list [expr [llength $trans]-$len] -1 -1]
     } else {
      if {$stressPositions != ""} {
       set stressPositions [lreplace $stressPositions 2 2 [expr [llength $trans]-$len]]
      }
     }
    } else {
     set stressPositions ""
    }
   }
   # specialfix för professor-er
   if {[regexp {[st]or$} [lindex $stemRes end-1]] && \
	   [lsearch {er ers erna ernas} [lindex $stemRes end]] > -1} {
    # 'ä s o r -> ä s 'o2: r ...
    if {[regsub {(.*)2:(\s\S+\so)(\sr.*)} $trans {\1\22:\3} trans] == 0} {
     regsub {(.*)(\s\S+\so)(\sr.*)} $trans {\1\22:\3} trans
    }
    if {$doStress} {
     if {[lindex $stressPositions 2] == -1} {
      set stressPositions [list [expr [lindex $stressPositions 0] + 2] -1 -1]
     } else {
      if {$stressPositions != ""} {
       set stressPositions [lreplace $stressPositions 2 2 [expr [lindex $stressPositions 2] + 2]]
      }
     }
    } else {
     set stressPositions ""
    }
   }
   if {[lsearch {alitet itet} [lindex $stemRes end]] > -1} {
    if {$doStress} {
     set stressPositions [list [expr [llength $trans]-2] -1 -1]
    } else {
     set stressPositions ""
    }
   }
   lappend resl [addStressMarkers $trans $stressPositions]
   return [list $resl $posTag]
  }
 }
 
 # Handle compounds
 if {([info exists ltsw($lang,$loword)] == 0 && $doDivide == 1) || \
	 [string match *-* $word]} {
  set components [divide $word]
  set components [fixKlassymbol $components]

  set skip 0
  # if set to 1 will interpret all Words as names and skip decompounding
  if 0 {
   set first [string tolower [lindex $components 0]]
   if {[string is upper [string index $word 0]] && \
	   [info exists ::n($first)] == 0 && \
	   [info exists lts($lang,$first)] == 0} {
    set skip 1
   }
   set last [string tolower [lindex $components end]]
   if {[string is upper [string index $word 0]] && \
	   [info exists ::n($last)] == 0 && \
	   [info exists lts($lang,$last)] == 0} {
    set skip 1
   }
  }
  if {$useDomain != "default"} {
   set skip 1
  }
  
  if {[llength $components] > 1 && $skip == 0} {
   if {$debug} { append trace "Decompound ($components), " }
   incr compound
   set tmp {}
   set tmpl {" "}
   set tmpl2 {}
   set insertHY 0
   foreach word $components {
    # variable is list, later integer, fix...
    lappend lenUpToLast [llength [lindex $tmpl2 0]]
    if {$word == "s" && [string match {*rn s *} $components]} {
     set transl "rs"
    } elseif {$word == "s" && [string match {*rd s *} $components]} {
     set transl "rs"
    } elseif {$word == "s" && [string match {*r s *} $components]} {
     set transl "rs"
     # bytte $lenUpToLast mot [llength $lenUpToLast]
     # fungerar, men borde nog ligga högre upp, som en- mfl
     # kommenterade bort, en massa "ë n" blev till "e:2 n", varför?
    } elseif {$word == "s" && [llength $lenUpToLast] > 1} {
     set transl "s"
    } elseif {$word == "er" && [llength $lenUpToLast] > 1} {
     set transl [list "ë r"]
    } elseif {$word == "en" && [llength $lenUpToLast] > 1} {
     set transl [list "ë n"]
    } elseif {$word == "et" && [llength $lenUpToLast] > 1} {
     set transl [list "ë t"]
    } elseif {$word == "t" && [llength $lenUpToLast] > 1} {
     set transl [list "t"]
    } else {
     if {[string is upper [string index $word 0]] && \
	     [info exists ::n([string tolower $word])]} {
      set transl {}
      # if ::n() exists and namelike use ::n()
      lappend transl [transLex2Tcl [string tolower $word] $::n([string tolower $word]) $lang]
      #puts $word,$trans,[list $a1 $b1 $c1]
     } else {
      # default case
      foreach {transl posTag} [transcribe -stress 0 -divide 0 -multiple $doMultiple $word] break
      #      if {$word == "läsår"} { puts $transl,[lindex $transl end],[string match "*$word s *" $components],$components }
      if {[lindex $transl end end] == "r" && \
	      [string match "*$word s *" $components]} {
       lset transl 0 end rs
#puts AAAAAAA,$transl
       set transl [list [concat [lrange [lindex $transl 0] 0 end-1]]]
#puts AAAAAAA,$transl
      }
     }
     if {$insertHY} {
      set transl [list [concat "-" [lindex $transl 0]]]
     }
     set insertHY 1
    }
    set tmpl2 {}
    foreach e $tmpl {
     foreach e2 $transl {
      lappend tmpl2 [string trim "$e $e2"]
     }
    }
    #      set tmpl2 [linsert $tmpl2  end-1 "hy"]
    set tmpl $tmpl2
   }
   set tmp $tmpl
   if {$doStress == 0} {
    set resl $tmp
   } else {
    set compdebug 0
    set tmp [lindex $tmp 0]

    # stress for first compound element
    # first component stress 1/2 -> stress 2

    if 1 {
     if {0&&[lindex $components 0] == "anti"} {
      set index 1
      set firstLen 5
     } elseif {0&&[lindex $components 0] == "pre"} {
      set index 1
      set firstLen 4
     } elseif {0&&[lindex $components 0] == "trans"} {
      set index 1
      set firstLen 6
     } else {
      set index 0
      set firstLen 0
     }
    }

    set stressPositions [lindex [stress $lang [lindex $components $index]] 0]
    if $compdebug {
     puts "StrComp: [lindex $components $index],$stressPositions"
    }
    #   puts $stressPositions,[lindex $components $index]
    # first component stress 1 -> stress 2
    if {[lindex $stressPositions 0] > -1 && [lindex $stressPositions 1] ==-1} {
     if $compdebug { puts "  comp_0 stress 1 -> compound stress 2" }
     set stressPositions [list -1 \
			      [expr $firstLen+[lindex $stressPositions 0]] -1]
    }
    # first component stress 2 -> stress 2
    if {[lindex $stressPositions 0] > -1 && [lindex $stressPositions 1] > -1} {
     if $compdebug { puts "  comp_0 stress 2 -> compound stress 2" }
     set stressPositions [list -1 \
			      [expr $firstLen+[lindex $stressPositions 1]] -1]
    }

    # stress for last compound element
    # if element has accent I make that secondary stress
    # if element has accent II and has secondary stress reuse that
    # if element has accent II use that as secondary stress
    # if element is ism/ist give it accent I
    # last component could be a stem, should not get stress
    set lastComp [lindex $components end]
    if {[lsearch $endings $lastComp] > -1&&[string match *itet $lastComp]==0} {
     set lastComp [lindex $components end-1]
     set lenUpToLast [lindex $lenUpToLast end-1]
    } else {
     set lenUpToLast [lindex $lenUpToLast end]
    }
    set tmpstr [lindex [stress $lang $lastComp] 0]
    # Must adjust indices if hy was inserted
    # code for insertHY seems weird, investigate...
    if {$insertHY} {
     incr lenUpToLast
    }
    if $compdebug {
     puts "  comp_0+hy-len: $lenUpToLast insertHY: $insertHY"
     puts "  [lindex $components $index]:$stressPositions | $lastComp:$tmpstr"
    }
    if {[lsearch {isering iserings iseringen iseringens iseringar iseringarna iseringars iseringarnas} $lastComp] > -1} {
     set stressPositions [list [expr $lenUpToLast+2] -1 -1]
     # a2: l - i s e2: r i ng -> a l - i s e2: r i ng
     regsub {(.*)2:(\s.\s\-\si\ss\se2:\sr\si\s.*)} $tmp {\1\2} tmp
    } elseif {[lsearch {ism isms ismen ismens ismer ismers ismerna ismernas ist ists isten istens ister isters isterna isternas} \
		   $lastComp] > -1} {
     set stressPositions [list $lenUpToLast -1 -1]
    } elseif {[lindex $tmpstr 0] > -1} {
     if $compdebug { puts "  comp_end stress 1 -> compound stress 3" }
     set stressPositions [lreplace $stressPositions 2 2 \
			      [expr $lenUpToLast + [lindex $tmpstr 0]]]
    } else {
     if $compdebug { puts "  comp_end stress 2 -> compound stress 3" }
     if {[lindex $tmpstr 2] > -1} {
      set stressPositions [lreplace $stressPositions 2 2 \
			       [expr $lenUpToLast + [lindex $tmpstr 2]]]
     } else {
      set stressPositions [lreplace $stressPositions 2 2 \
			       [expr $lenUpToLast + [lindex $tmpstr 1]]]
     }
    }
    if {[lsearch {ett två tre fem sex sju tolv} $lastComp] > -1} {
     set stressPositions [list [lindex $stressPositions 2] -1 -1]
    }
    if {[lsearch {fyra åtta tio elva tretton fjorton femton sexton sjutton arton nitton} $lastComp] > -1 || [regexp {^tjugo|^trettio|^fyrtio|^femtio|^sextio|^sjuttio|^åttio|^nittio} $lastComp]} {
     set stressPositions [lindex [stress $lang [lindex $components end]] 0]
     if {[lindex $stressPositions 0] != -1} {
      set stressPositions [list [expr $lenUpToLast + [lindex $stressPositions 0]] -1 -1]
     } elseif {[lindex $stressPositions 2] == -1} {
      set stressPositions [list -1 [expr $lenUpToLast + [lindex $stressPositions 1]] -1]
     } else {
      set stressPositions [list -1 [expr $lenUpToLast + [lindex $stressPositions 1]] [expr $lenUpToLast + [lindex $stressPositions 2]]]
     }
    }
    if $compdebug { puts "  $tmp $stressPositions" }
    set resl [list [addStressMarkers $tmp $stressPositions]]
    if $compdebug { puts "StrComp: out:$resl" }
   }
   if {[info exists ltsp($lang,[lindex $components end])]} {
    set posTag [lindex $ltsp($lang,[lindex $components end]) 0]
   }
   return [list $resl $posTag]
  }
 } ;# if {[info exists ltsw($lang,$loword)] == 0 && $doDivide == 1}
 
 # LTS by CART
 if {$useDomain == "default"} {
  if {$debug} { append trace "CART, " }
  set res [cart $lang $word]
  lappend resl [addStressMarkers $res [lindex $stressPositions 0]]
  set posTag XXX
 } elseif {$useDomain == "name"} {
  if {$debug} { append trace "Name-CART, " }
  set res [cart pn $word]
  if {$doStress == 0} {
   set stressPositions ""
  } else {
   set stressPositions [stress pn $loword]
  }
  lappend resl [addStressMarkers $res [lindex $stressPositions 0]]
  set posTag PM
 } elseif {$useDomain == "english"} {
  if {$debug} { append trace "English-CART, " }
  set res [cart en $word]
  if {$doStress == 0} {
   set stressPositions ""
  } else {
   set stressPositions [stress en $loword]
  }
  lappend resl [addStressMarkers $res [lindex $stressPositions 0]]
  set posTag PM
 } else {
  error "Unknown CART domain:$useDomain"
 }
 return [list $resl $posTag]
}

proc lts::cart {domain word} {
 #puts [info level 0]
 variable featVect
 variable ltsTree
 set res ""
 set loword [string tolower $word]
 set loword [string map {ø ö æ ä á a à a è e í i ì i} $loword]
 set pre 4
 set post 4
 set tmp [string repeat _ $pre]
 append tmp $loword
 append tmp [string repeat _ $post]
 set tmp [split $tmp {}]
 set last ""
 for {set i 0} {$i < [string length $loword]} {incr i} {
  if {[info exists ltsTree($domain,[lindex $tmp [expr $i+$pre]])]} {
   set featVect [lrange $tmp [expr $i] [expr $i+$pre+$post]]
   lappend featVect $word
   if {$i > 0} {append res " "}
   set predictedPhone [walkTree $ltsTree($domain,[lindex $tmp [expr $i+$pre]])]
   if {$last != $predictedPhone} {
    append res $predictedPhone
   }
   set last $predictedPhone
  }
 }
 regsub -all "_" $res " " res
 regsub -all " eps|eps " $res "" res
 return $res
}
# not used?
proc lts::cartName {word} {
 variable lang
 variable featVect
 variable ltsTreeN
 set res ""
 set loword [string tolower $word]
 set loword [string map {ø ö æ ä á a à a è e í i ì i} $loword]
 set pre 4
 set post 4
 set tmp [string repeat _ $pre]
 append tmp $loword
 append tmp [string repeat _ $post]
 set tmp [split $tmp {}]
 set last ""
 for {set i 0} {$i < [string length $loword]} {incr i} {
  if {[info exists ltsTreeN($lang,[lindex $tmp [expr $i+$pre]])]} {
   set featVect [lrange $tmp [expr $i] [expr $i+$pre+$post]]
   lappend featVect $word
   if {$i > 0} {append res " "}
   set predictedPhone [walkTree $ltsTreeN($lang,[lindex $tmp [expr $i+$pre]])]
   if {$last != $predictedPhone} {
    append res $predictedPhone
   }
   set last $predictedPhone
  }
 }
 regsub -all "_" $res " " res
 regsub -all " eps|eps " $res "" res
 return $res
}

proc lts::transUS {word stressPositions} {
 #puts [info level 0]
 variable lang
 variable lts

 set resl {}

 # check if the word is among exceptions
 if {[info exists lts($lang,$word)]} {
  lappend resl [lindex $lts($lang,$word) 0]
  return [list $resl DUMMY]
 }

 # LTS by CART
 
 set res [cart $lang $word]
 set strpos [lindex $stressPositions 0]
 if {$strpos == -1} { set strpos 0 } ;# fix for word "exiles"
 lappend resl [addStressMarkers $res $strpos]
 return [list $resl DUMMY]
}

# Sub-optimal solution, works for now, obsolete?!?!? ::stress does the same?
proc lts::stresslist {args} {
 array set a [list \
		  -task recognition \
		 ]
 array set a [lreplace $args end end]
 set task $a(-task)
 set word [lindex $args end]

 set trans  [lindex [lts::transcribe -multiple 1 $word] 0]
 set transb [lindex [lts::transcribe -multiple 1 -stress 1 $word] 0]
 
 if {[string match rec* $task]} {
  set trans  [lts::syn2rec $trans]
  set transb [lts::syn2rec $transb]
 }
 set res {}
 
 for {set i 0} {$i < [llength $trans]} {incr i} {
  set tmp {}
  foreach s [list 1 2 3] {
   lappend tmp [lsearch [lindex $transb $i] *_$s]
  }
  lappend res $tmp
 }
 set res
}

proc lts::isAcronym word {
 variable lang

 # words with consonants only are probably acronyms
 if {[regexp {^[bcdfghjklmnpqrstvwxzBCDFGHJKLMNPQRSTVWXZ]+$} $word]} { return 1 }
 if {[regexp {^[A-ZÅÄÖ]+$} $word]} { return 1 }
 if {[regexp {^[A-ZÅÄÖ]+\:s$} $word]} { return 1 }

 # word exists in lexicon, use that pronunciation, should always be correct
 if {[info exists ltsw($lang,[string tolower $word])]} { return 0 }

 # acronyms like FoU
 if {[regexp {^[A-ZÅÄÖ].+[A-ZÅÄÖ].*$} $word] && [string length $word] < 8} {
  return 1
 }

 # disallow non-letters
 if {[regexp {^[\w]+$} $word] == 0} { return 0 }

 # statement cannot have effect
 if {[regexp {.*[A-ZÅÄÖ]+.*} $word] && [regexp {.*[\d]+.*} $word]} { return 1 }
 return 0
}

proc lts::isFunctionWord word {
 variable funcWords
 if {[lsearch $funcWords $word] >= 0} { return 1 }
 return 0
}

proc lts::spell word {
 variable spell
 set res ""
 foreach l [split $word ""] {
  if {$l == "s"} {
   append res " s"
  } else {
   set l  [string tolower $l]
   if {[lsearch {"-" "é" "'" ":"} $l] >= 0} {
    puts stderr FIXA:$word
    continue
   }
   append res " $spell($l)"
  }
 }
 return [string trim $res]
}




proc lts::syn2rec {list} {
 set res {}
 foreach str $list {
  # time to make this code nicer!
  regsub -all " P " $str " P p " str
  regsub -all " T " $str " T t " str
  regsub -all " K " $str " K k " str
  regsub -all " B " $str " B b " str
  regsub -all " D " $str " D d " str
  regsub -all " G " $str " G g " str
  
  # ugly hack
  regsub -all " P p P " $str " P p P p " str
  regsub -all " T t T " $str " T t T t " str
  regsub -all " K k K " $str " K k K k " str
  regsub -all " B b B " $str " B b B b " str
  regsub -all " D d D " $str " D d D d " str
  regsub -all " G g G " $str " G g G g " str
  
 # transcribe case
  regsub -all "^P " $str "P p " str
  regsub -all "^T " $str "T t " str
  regsub -all "^K " $str "K k " str
  regsub -all "^B " $str "B b " str
  regsub -all "^D " $str "D d " str
  regsub -all "^G " $str "G g " str
  
  regsub -all " P$" $str " P p" str
  regsub -all " T$" $str " T t" str
  regsub -all " K$" $str " K k" str
  regsub -all " B$" $str " B b" str
  regsub -all " D$" $str " D d" str
  regsub -all " G$" $str " G g" str

  list {  
   # multrans case
   regsub -all "{P " $str "{P p " str
   regsub -all "{T " $str "{T t " str
   regsub -all "{K " $str "{K k " str
   regsub -all "{B " $str "{B b " str
   regsub -all "{D " $str "{D d " str
   regsub -all "{G " $str "{G g " str
   
   regsub -all " P}" $str " P p}" str
   regsub -all " T}" $str " T t}" str
   regsub -all " K}" $str " K k}" str
   regsub -all " B}" $str " B b}" str
   regsub -all " D}" $str " D d}" str
   regsub -all " G}" $str " G g}" str
  }

  regsub -all "RT" $str "RT t" str
  regsub -all "RD" $str "RD d" str
  
  # obsolete??!??! valid when det -> D i mulpron
  regsub -all "^D$" $str "D d" str
  lappend res $str
 }
 regsub -all {\s\-|\s\_} $res "" res 
 set res
}

# ny version för tpa, input should be {{h e j}} a la lts::transcribe
proc lts::syn2rec {list} {
 #puts [info level 0]
 set res {}
 foreach str $list {

  regsub -all { p([:\.]*) } $str { pcl p\1 } str
  regsub -all { t([:\.]*) } $str { tcl t\1 } str
  regsub -all { k([:\.]*) } $str { kcl k\1 } str
  regsub -all { b([:\.]*) } $str { bcl b\1 } str
  regsub -all { d([:\.]*) } $str { dcl d\1 } str
  regsub -all { g([:\.]*) } $str { gcl g\1 } str
  
  # ugly hack, när behövs detta?!?
  regsub -all { pcl p p } $str { pcl p pcl p } str
  regsub -all { tcl t t } $str { tcl t tcl t } str
  regsub -all { kcl k k } $str { kcl k kcl k } str
  regsub -all { bcl b b } $str { bcl b bcl b } str
  regsub -all { dcl d d } $str { dcl d dcl d } str
  regsub -all { gcl g g } $str { gcl g gcl g } str
  
 # transcribe case
  regsub -all {^p([:\.]*) } $str {pcl p\1 } str
  regsub -all {^t([:\.]*) } $str {tcl t\1 } str
  regsub -all {^k([:\.]*) } $str {kcl k\1 } str
  regsub -all {^b([:\.]*) } $str {bcl b\1 } str
  regsub -all {^d([:\.]*) } $str {dcl d\1 } str
  regsub -all {^g([:\.]*) } $str {gcl g\1 } str
  
  regsub -all { p([:\.]*)$} $str { pcl p\1} str
  regsub -all { t([:\.]*)$} $str { tcl t\1} str
  regsub -all { k([:\.]*)$} $str { kcl k\1} str
  regsub -all { b([:\.]*)$} $str { bcl b\1} str
  regsub -all { d([:\.]*)$} $str { dcl d\1} str
  regsub -all { g([:\.]*)$} $str { gcl g\1} str

  regsub -all {rt} $str {rtc rt} str
  regsub -all {rd} $str {rdc rd} str
  regsub -all {j3} $str {dcl j3} str
  regsub -all {tdcl j3} $str {tcl tj3} str ;# not so beautiful...
  regsub -all {sdcl j3} $str {sj3} str ;# not so beautiful...

  if 0 {
   # hack to use Swedish HMMs with english
   regsub -all {er} $str {E0 R} str
   regsub -all {dh} $str {J} str
   regsub -all {jh} $str {T t J} str
   regsub -all {th} $str {F} str
   regsub -all {ch} $str {TJ} str
   regsub -all {sh} $str {RS} str
   regsub -all {zh} $str {RS} str
   regsub -all {s} $str {S} str
   regsub -all {pcl} $str {P} str
   regsub -all {tcl} $str {T} str
   regsub -all {kcl} $str {K} str
   regsub -all {bcl} $str {B} str
   regsub -all {dcl} $str {D} str
   regsub -all {gcl} $str {G} str
   regsub -all {r} $str {R} str
   regsub -all {z} $str {S} str
   regsub -all {l} $str {L} str
   regsub -all {v} $str {V} str
   regsub -all {uw} $str {U:} str
   regsub -all {uh} $str {U} str
   regsub -all {ng} $str {NG} str
   regsub -all {n} $str {N} str
   regsub -all {m} $str {M} str
   regsub -all {f} $str {F} str
   regsub -all {iy} $str {I} str
   regsub -all {ae} $str {Ä} str
   regsub -all {ao} $str {Å} str
   regsub -all {ow} $str {O:} str
   regsub -all {oy} $str {Å J} str
   regsub -all {aw} $str {Å:} str
   regsub -all {w} $str {O:} str
   regsub -all {ah} $str {A} str
   regsub -all {eh} $str {A} str
   regsub -all {ey} $str {Ä J} str
   regsub -all {ay} $str {A J} str
   regsub -all {aa} $str {A} str
   regsub -all {ih} $str {I} str
   regsub -all {hh} $str {H} str
   regsub -all {y} $str {J} str
   # end hack  
  }
  if 0 {
   # hack to use Swedish HMMs with English
   regsub -all {er} $str {er1 er2} str
   regsub -all {jh} $str {jh1 jh2 jh3} str
   regsub -all {oy} $str {oy1 oy2} str
   regsub -all {ey} $str {ey1 ey2} str
   regsub -all {ay} $str {ay1 ay2} str
   # end hack  
  }
 lappend res $str
 }
 regsub -all {\s\-|\s\_} $res "" res 
 
 #puts A,$res
 set res2 {}
 foreach e1 $res {
  foreach e2 $e1 {
   lappend res2 $e2
  }
 }
 #puts B,[list $res2]
 return [list $res2]
}

proc lts::rec2syn {str} {
 regsub -all "P p" $str "P" str
 regsub -all "T t" $str "T" str
 regsub -all "K k" $str "K" str
 regsub -all "B b" $str "B" str
 regsub -all "D d" $str "D" str
 regsub -all "G g" $str "G" str
  
 set str
}


proc lts::number2text n {

 regsub {(\d\d)(\d\d)(\d\d)} $n {\1 \2 \3} n

 regsub {(\d\d)(\d\d\d)} $n {\1 \2} n

 if 1 {
  regsub {18(\d\d)} $n {arton hundra \1} n
 }
 regsub {19(\d\d)} $n {nitton hundra \1} n
 regsub {2000} $n {tjugo hundra} n
 regsub {200(\d)} $n {tjugo hundra \1} n

 regsub {1(\d\d\d)} $n {ett tusen \1} n
 regsub {3(\d\d\d)} $n {tre tusen \1} n
 regsub {4(\d\d\d)} $n {fyra tusen \1} n
 regsub {5(\d\d\d)} $n {fem tusen \1} n
 regsub {6(\d\d\d)} $n {sex tusen \1} n
 regsub {7(\d\d\d)} $n {sju tusen \1} n
 regsub {8(\d\d\d)} $n {åtta tusen \1} n
 regsub {9(\d\d\d)} $n {nio tusen \1} n
 regsub {(\w+\s\w+\s)000} $n {\1} n

 regsub {1(\d\d)} $n {ett hundra \1} n
 regsub {2(\d\d)} $n {två hundra \1} n
 regsub {3(\d\d)} $n {tre hundra \1} n
 regsub {4(\d\d)} $n {fyra hundra \1} n
 regsub {5(\d\d)} $n {fem hundra \1} n
 regsub {6(\d\d)} $n {sex hundra \1} n
 regsub {7(\d\d)} $n {sju hundra \1} n
 regsub {8(\d\d)} $n {åtta hundra \1} n
 regsub {9(\d\d)} $n {nio hundra \1} n
 regsub {(\w+\s\w+\s)00} $n {\1} n
 regsub {(\w+\s\w+\s)0(\w+)} $n {\1\2} n

 regsub {10} $n {tio} n
 regsub {11} $n {elva} n
 regsub {12} $n {tolv} n
 regsub {13} $n {tretton} n
 regsub {14} $n {fjorton} n
 regsub {15} $n {femton} n
 regsub {16} $n {sexton} n
 regsub {17} $n {sjutton} n
 regsub {18} $n {arton} n
 regsub {19} $n {nitton} n
 regsub {20} $n {tjugo} n
 regsub {30} $n {trettio} n
 regsub {40} $n {fyrtio} n
 regsub {50} $n {femtio} n
 regsub {60} $n {sextio} n
 regsub {70} $n {sjuttio} n
 regsub {80} $n {åttio} n
 regsub {90} $n {nittio} n
 regsub {2(\d)} $n {tjugo\1} n
 regsub {3(\d)} $n {trettio\1} n
 regsub {4(\d)} $n {fyrtio\1} n
 regsub {5(\d)} $n {femtio\1} n
 regsub {6(\d)} $n {sextio\1} n
 regsub {7(\d)} $n {sjuttio\1} n
 regsub {8(\d)} $n {åttio\1} n
 regsub {9(\d)} $n {nittio\1} n

 regsub 1 $n "ett" n
 regsub 2 $n "två" n
 regsub 3 $n "tre" n
 regsub 4 $n "fyra" n
 regsub 5 $n "fem" n
 regsub 6 $n "sex" n
 regsub 7 $n "sju" n
 regsub 8 $n "åtta" n
 regsub 9 $n "nio" n
 regsub 0 $n "noll" n

 set n
}

proc lts::numeral2ordinal n {
 if {([regexp {.*1$} $n] || [regexp {.*2$} $n]) && $n != 11 && $n != 12} {
  append n ":a"
 } else {
  append n ":e"
 }
 set n
}

proc lts::ordinal2text n {
 set res ""
 if {[regexp {00\:} $n]} {
  set n [string trimright [string trimright [number2text $n] :ae]]de
 }
 if {[regexp {[123456789]0\:} $n]} {
  set n [string trimright [string trimright [number2text $n] :ae]]nde
 }
 if {[regexp {(\d+)(0\d)\:} $n dummy x y]} {
  set res [number2text ${x}00]
 } elseif {[regexp {(\d+)(\1\d)\:} $n dummy x y]} {
  set res [number2text ${x}00]
 } elseif {[regexp {(\d*[23456789])(\d)\:} $n dummy x y]} {
  set res [number2text ${x}0]
 }
 regsub {.*10\:e} $n "tionde" n
 regsub {.*11\:e} $n "elfte" n
 regsub {.*12\:e} $n "tolfte" n
 regsub {.*13\:e} $n "trettonde" n
 regsub {.*14\:e} $n "fjortonde" n
 regsub {.*15\:e} $n "femtonde" n
 regsub {.*16\:e} $n "sextonde" n
 regsub {.*17\:e} $n "sjuttonde" n
 regsub {.*18\:e} $n "artonde" n
 regsub {.*19\:e} $n "nittonde" n
 regsub {.*1\:a} $n "första" n
 regsub {.*1\:e} $n "förste" n
 regsub {.*2\:a} $n "andra" n
 regsub {.*2\:e} $n "andre" n
 regsub {.*3\:e} $n "tredje" n
 regsub {.*4\:e} $n "fjärde" n
 regsub {.*5\:e} $n "femte" n
 regsub {.*6\:e} $n "sjätte" n
 regsub {.*7\:e} $n "sjunde" n
 regsub {.*8\:e} $n "åttonde" n
 regsub {.*9\:e} $n "nionde" n
 regsub {.*0\:e} $n "nollte" n

 append res $n
 regsub -all " " $res "" res
 set res
}

# Compare two phones while disregarding : and .

proc lts::equalPhone {a b} {
 string equal [string trim $a .:] [string trim $b .:] 
}

# inter word pronunciation transformations
# intra word for the case x x -> x

proc lts::coart {w nw wtransV nwtransV stressV nstressV} {
 upvar $wtransV  wtrans
 upvar $nwtransV nwtrans
 upvar $stressV  stress
 upvar $nstressV nstress
 
 # Assimilation standard case
 set tmp $wtrans
 set tmps $stress
 foreach e $wtrans s $stress {
  foreach f $nwtrans {
   if {[equalPhone [lindex $e end] [lindex $f 0]]} {
    set new [lrange $e 0 end-1]
    if {[lsearch $tmp $new] == -1} {
     lappend tmp  $new
     lappend tmps $s
    }
   }
  }
 }
 set wtrans $tmp
 set stress $tmps

 # Assimilation for plosives
 set tmp $wtrans
 set tmps $stress
 foreach e $wtrans s $stress {
  foreach f $nwtrans {
   if {[lsearch {k p t b d g k. p. t. b. d. g. k: p: t: b: d: g:} \
	    [lindex $e end]] >= 0} {
    if {[equalPhone [lindex $e end] [lindex $f 1]] &&
    [lindex $e end-1] == [lindex $f 0]} {
     set new [lrange $e 0 end-2]
     if {[lsearch $tmp $new] == -1} {
      lappend tmp $new
      lappend tmps $s
     }
    }
   }
  }
 }
 set wtrans $tmp
 set stress $tmps

 # R X -> RX
 set tmp $wtrans
 set tmps $stress
 set ntmp $nwtrans
 set ntmps $nstress
 foreach e $wtrans s $stress {
  foreach f $nwtrans ns $nstress {
   foreach {l r n} [list r d rd r l rl r n rn r s rs r t rt] {
    if {[equalPhone $l [lindex $e end]] && \
      [equalPhone $r [lindex $f 0]]} {
     set new [lrange $e 0 end-1]
     if {[lsearch $tmp $new] == -1} {
      lappend tmp $new
      lappend tmps $s
     }
     set new [lreplace $f 0 0 $n]
     if {[lsearch $ntmp $new] == -1} {
      lappend ntmp $new
      lappend ntmps $ns
     }
    }
   }
  }
 }
 set wtrans $tmp
 set nwtrans $ntmp
 set stress $tmps
 set nstress $ntmps

 # en bil -> em bil
 set tmp $wtrans
 set tmps $stress
 foreach e $wtrans s $stress {
  foreach f $nwtrans {
   foreach {l r n} [list n b m n g ng n k ng n j ng] {
    if {[equalPhone $l [lindex $e end]] && \
	    [equalPhone $r [lindex $f 0]]} {
     set new [lreplace $e end end $n]
     if {[lsearch $tmp $new] == -1} {
      lappend tmp $new
      lappend tmps $s
     }
    }
   }
  }
 }
 set wtrans $tmp
 set stress $tmps

 # uttala /U:TTA:LA/ -> /U:TA:LA/
 set tmp $wtrans
 set tmps $stress
 foreach e $wtrans s $stress {
  for {set i 0} {$i < [expr [llength $e]-1]} {incr i} {
   # X X -> X
   if {[lindex $e $i] == [lindex $e [expr $i+1]]} {
    lappend tmp [lreplace $e $i $i]
    set tmpss {}
    foreach se $s {
     if {$se > $i} {
      incr se -1
     }
     lappend tmpss $se
    }
    lappend tmps $tmpss
    break
   }
   list {
    # X x X x -> X x
    if {$i < [expr [llength $e]-3]} {
     if {[lindex $e $i] == [lindex $e [expr $i+2]] && \
	     [lindex $e [expr $i+1]] == [lindex $e [expr $i+3]] && \
	     [string is lower [lindex $e [expr $i+1]]]} {
      lappend tmp [lreplace $e $i [expr $i+1]]
      set tmpss {}
      foreach se $s {
       if {$se > $i} {
	incr se -2
       }
       lappend tmpss $se
      }
      lappend tmps $tmpss
      break     
     }
    }
   }

   # Y y X x -> X x
   if {$i < [expr [llength $e]-3]} {
    if {[regexp {^pcl$|^tcl$|^kcl$|^bcl$|^dcl$|^gcl$|^rdc$|^rtc$} [lindex $e $i]] && \
      [lindex $e [expr $i+1]] == [lindex $e [expr $i+3]] && \
      [string is lower [lindex $e [expr $i+1]]]} {
     lappend tmp [lreplace $e $i [expr $i+1]]
     set tmpss {}
     foreach se $s {
      if {$se > $i} {
       incr se -2
      }
      lappend tmpss $se
     }
     lappend tmps $tmpss
     break     
    }
   }

  }
 }
 set wtrans $tmp
 set stress $tmps

 # -igt -> -ikt -it, not actual coarticulation, but convenient place to put fix
 set tmp $wtrans
 set tmps $stress
 foreach e $wtrans s $stress {
  if {[string match "* i gcl g tcl t" $e]} {
  # 060919 temporarily disallow this alternative
  # foreach new [list [lreplace $e end-3 end kcl k tcl t] [lreplace $e end-3 end tcl t]]
   foreach new [list [lreplace $e end-3 end kcl k tcl t]] {
    if {[lsearch $tmp $new] == -1} {
     lappend tmp  $new
     lappend tmps $s
    }
   }
  }
  # 060919 temporarily disallow this alternative
  if {0&&[string match "* i s kcl k tcl t" $e]} {
   set new [lreplace $e end-3 end tcl t]
   if {[lsearch $tmp $new] == -1} {
    lappend tmp  $new
    lappend tmps $s
   }
  }
 }


 set wtrans $tmp
 set stress $tmps

 if {0 && ($w == "är" || $w == "på") && $nw == "den"} {
  lappend nwtrans [list r ë n]
  lappend nstress [list 1 -1 -1]
 }

 if {0 && $w != "" && $nw == "du"} {
  lappend nwtrans [list U:] [list U] [list R U:]
  lappend nstress [list 0 -1 -1] [list 0 -1 -1] [list 1 -1 -1]
 }

 if {0 && $w == "tror" && $nw == "det"} {
  lappend wtrans [list tcl t r o2:]
  lappend stress [list 3 -1 -1]
  lappend nwtrans [list r ë] [list r e]
  lappend nstress [list -1 -1 -1] [list -1 -1 -1]
 }

}

proc lts::syllabifyWord word {
 variable syll

 set loword [string tolower $word]
 set seq [split $loword aeiouyåäöéü]
 regsub -all {a|e|i|o|u|y|å|ä|ö|ü|é} $loword { & } str
 set str [split $str]

 for {set i 2} {$i < [llength $str] - 1} {incr i 2} {
  set cs   [lindex $str $i]

  set ccc  [string range $cs 0 2]
  set rest [string range $cs 3 end]
  if {[lsearch $syll(finCCC) $ccc] > -1} {
   set str [lreplace $str $i $i $ccc $rest]
   incr i
   continue
  }

  set ccc  [string range $cs end-2 end]
  set rest [string range $cs 0 end-3]
  if {[lsearch $syll(inCCC) $ccc] > -1} {
   set str [lreplace $str $i $i $rest $ccc]
   incr i
   continue
  }

  set cc   [string range $cs 0 1]
  set rest [string range $cs 2 end]
  if {[lsearch $syll(finCC) $cc] > -1} {
   set str [lreplace $str $i $i $cc $rest]
   incr i
   continue
  }

  set cc   [string range $cs end-1 end]
  set rest [string range $cs 0 end-2]
  if {[lsearch $syll(inCC) $cc] > -1} {
   set str [lreplace $str $i $i $rest $cc]
   incr i
   continue
  }

  set str [lreplace $str $i $i {} $cs]
  incr i
 }
 set res {}
 foreach {a b c} $str {
  lappend res $a$b$c
 }
 set res
}

# Cut here
# Self-tests

if {[info exists argv] == 0 || [lindex $argv 0] != "test"} return

lts::init se full.tree full.except
#lts::init se suot.tree suot.except

if {[llength $argv] > 1} {
 puts [lts::transcribe -stress 1 -multiple 1 [lindex $argv 1]]
 return
}

set ok 1
set tests [list \
	       "" "{}" 0 0 \
	       "han" "{H A N}" 0 0 \
	       "HAN" "{H A N}" 0 0 \
	       "Festival" "{F E S T I V A: L}" 0 0 \
	       "festival" "{F E S T I V A: L}" 0 0 \
	       "FESTIVAL" "{F E S T I V A: L}" 0 0 \
	       "AB2" "{A: B E: T V Å:}" 0 0 \
	       "s" "{E S}" 0 0 \
	       "fortfarande" "{F O RT F A: R A N D E0}" 0 0 \
	       "snmp" "{E S E N E M P E:}" 0 0 \
	       "1875" "{A: RT Å N H U N D R A SJ U T I F E M}" 0 0 \
	       "2002" "{TJ U: G O H U N D R A T V Å:}" 0 0 \
	       "9" "{N I: O} {N I: E}" 0 1 \
	       "9" "{N I:_2 O} {N I:_2 E}" 1 1 \
	       "nio" "{N I: O} {N I: E}" 0 1 \
	       "nio" "{N I:_2 O} {N I:_2 E}" 1 1 \
	       "9210" "{N I: O T U: S E0 N T V Å: H U N D R A T I: O} {N I: O T U: S E0 N T V Å: H U N D R A T I: E} {N I: E T U: S E0 N T V Å: H U N D R A T I: O} {N I: E T U: S E0 N T V Å: H U N D R A T I: E}" 0 1 \
	       "lålálälö" "{L Å: L A L Ä: L Ö:}" 0 0 \
	       "eftermiddag" "{E F T E0 R M I D A: G} {E F T E0 R M I D A} {E F T E0 M I D A}" 0 1 \
	       "och" "{Å K}" 0 0 \
	       "och" "{Å_1 K}" 1 0 \
	       "och" "{Å K} Å: Å" 0 1 \
	       "och" "{Å_1 K} Å:_1 Å_1" 1 1 \
	       "advokatbyråer" "{A D V O K A: T B Y: R Å: E0 R}" 0 0 \
	       "advokatbyråer" "{A D V O K A:_2 T B Y: R Å:_3 E0 R}" 1 0 \
	       "O2" "{O: T V Å:}" 0 0 \
	       "O2" "{O: T V Å:_1}" 1 0 \
	       "O2-dosering" "{O: T V Å: D O S E: R I NG}" 0 0 \
	       "spaken" "{S P A: K E0 N}" 0 0 \
	       "spaken!" "{S P A: K E0 N}" 0 0 \
	       "spaken?" "{S P A: K E0 N}" 0 0 \
	       "spaken," "{S P A: K E0 N}" 0 0 \
	       "spaken;" "{S P A: K E0 N}" 0 0 \
	       "spaken." "{S P A: K E0 N}" 0 0 \
	       "m" "{E M}" 0 0 \
	       "centerriksdagsmannen" \
	       "{S E_2 N T E0 R R I K S D A: G S M A_3 N E0 N}" 1 1 \
	       "husmus" "{H U:_2 S M U:_3 S}" 1 1 \
	       "beredskapsläge" "{B E R E: D S K A: P S L Ä: G E0}" 0 0 \
	       "allmän" "{A_2 L M Ä_3 N}" 1 1 \
	       "allmänbildande" "{A_2 L M Ä N B I_3 L D A N D E0}" 1 1 \
	      ]

# These belong above, errors right now
#	       "oxygen-" "{O K S Y J E: N}" 0 0 \
	       "2002" "{TJ U:_2 G O H U N D R A T V Å:_1}" 1 0 \
	       "O2-dosering" "{O: T V Å: D O S E:_1 R I NG}" 1 0 \
	       "Fåhræus" "{F Å: R E: U S}" 0 0 \

foreach {word ref stress multiple} $tests {
 set tmp [lts::transcribe -stress $stress -multiple $multiple $word]
 if {$tmp != $ref} { puts "Error:$tmp != $ref"; set ok 0}
}



set ok 1
set tests [list \
	       "" "{}" \
	       "och" "{Å K k}" \
	      ]
# These belong above, errors right now
#	       "uttala" "{U: T t A: L A}" \

foreach {word ref} $tests {
 set tmp [lts::syn2rec [lts::transcribe $word]]
 if {$tmp != $ref} { puts "Error:$tmp != $ref"; set ok 0}
}


set ok 1
set tests [list \
  "{}" "{}" "{}" "{}" \
  "{H E J}" "{}" "{H E J}" "{}" \
  "{}" "{H E J}" "{}" "{H E J}" \
  "{E N}" "{B b I: L}" "{E N} {E M}" "{B b I: L}" \
  "{E N}" "{B b I: L} {G g U: L}" "{E N} {E M} {E NG}" "{B b I: L} {G g U: L}"\
  "{D E T t}" "{T t A: R}" "{D E T t} {D E}" "{T t A: R}" \
  "{A X} {A Y}" "{X B} {Y B}" "{A X} {A Y} A" "{X B} {Y B}" \
  "{A X} {B Y}" "{X C} {Y C}" "{A X} {B Y} A B" "{X C} {Y C}" \
  "{A K k} {B T t}" "{K k C} {T t C}" "{A K k} {B T t} A B" "{K k C} {T t C}" \
  "{A R} {B R}" "{N C} {S D}" "{A R} {B R} A B" "{N C} {S D} {RN C} {RS D}" \
  "{U: T T A: L A}" "{}" "{U: T T A: L A} {U: T A: L A}" "{}" \
  ]

foreach {left right refLeft refRight} $tests {
 # dummy? needs to be same length as left/right, we ignore content here
 set dummy1 {}
 foreach e $left { lappend dummy1 0}
 set dummy2 {}
 foreach e $right { lappend dummy2 0}
 lts::coart xxx xxx left right dummy1 dummy2
 if {$left != $refLeft} { puts "Error:$left != $refLeft"; set ok 0}
 if {$right != $refRight} { puts "Error:$right != $refRight"; set ok 0}
}




# fungerar ej
set tests [list \
	       "tidningarna" "tidningarna" \
	      ]
set tests [list \
	       "" "" \
	       "ö" "ö" \
	       "han" "han" \
	       "tralla" "tra lla" \
	       "sjunga" "sjung a" \
	       "räfst" "räfst" \
	       "räfsning" "räfs ning" \
	       "ragnar" "ragn ar" \
	       "andning" "and ning" \
	       "hjälpmedel" "hjälp me del" \
	       "handske" "hand ske" \
	       "utskrift" "ut skrift" \
	       "process" "pro cess" \
	       "matriser" "ma tri ser" \
	       "mamma" "ma mma" \
	       "pappa" "pa ppa" \
	       "glimmande" "gli mmand e" \
	       "strålande" "strå land e" \
	       "glittrande" "gli ttrand e" \
	       "schweizisk" "schwe i zisk" \
	       "kaka" "ka ka" \
	       "revansch" "re vansch" \
	       "omkring" "om kring" \
	       "västkustskt" "väst kustskt" \
	      ]
# These belong above, errors right now
#	       "hasta" "has ta" \

foreach {word ref} $tests {
 set tmp [lts::syllabifyWord $word]
 if {$tmp != $ref} { puts "Error:$tmp != $ref"; set ok 0}
}

# Make proper tests
list {
puts i:[lts::isFunctionWord i]
puts dag:[lts::isFunctionWord dag]
puts om:[lts::isFunctionWord om]
puts en:[lts::isFunctionWord en]
puts advokatbyråer:[lts::stresslist advokatbyråer]
puts nio:[lts::stresslist nio]
}

if $ok { puts "Internal tests ok" }


return
foreach word [list 3 4 11 20 29 90 100 101 111 199 999 1000 1001 1010 1101 1300 1737 1987 2000 2003 3000 3001 3010 3099 3400 3421 9999 10000 19762 197620] {
 puts $word:[lts::number2text $word]
}

#3.000
#3.14
#-5
#25-30 \d(-)\d till
#90-talet -> nittio talet problemfri!
# 10 januari 2003.
