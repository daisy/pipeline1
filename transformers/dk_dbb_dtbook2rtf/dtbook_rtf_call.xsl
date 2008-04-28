<?xml version="1.0" encoding="ISO-8859-1"?>
<!--
  Daisy Pipeline (C) 2005-2008 DBB and Daisy Consortium
  
  This library is free software; you can redistribute it and/or modify it under
  the terms of the GNU Lesser General Public License as published by the Free
  Software Foundation; either version 2.1 of the License, or (at your option)
  any later version.
  
  This library is distributed in the hope that it will be useful, but WITHOUT
  ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
  FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
  details.
  
  You should have received a copy of the GNU Lesser General Public License
  along with this library; if not, write to the Free Software Foundation, Inc.,
  59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
--> 
<xsl:stylesheet version="1.0" 
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:dtb="http://www.daisy.org/z3986/2005/dtbook/"
	>
	<xsl:output method="text" version="1.0" encoding="windows-1252"/>

	<xsl:template name="BIGHEADER">
	<xsl:text>{\rtf1\ansi\ansicpg1252\uc1 \deff0\deflang1033\deflangfe1030{\fonttbl{\f0\froman\fcharset0\fprq2{\*\panose 02020603050405020304}Times New Roman;}{\f1\fswiss\fcharset0\fprq2{\*\panose 020b0604020202020204}Arial;}
{\f7\fswiss\fcharset0\fprq2{\*\panose 00000000000000000000}Geneva{\*\falt Arial};}{\f54\froman\fcharset238\fprq2 Times New Roman CE;}{\f55\froman\fcharset204\fprq2 Times New Roman Cyr;}{\f57\froman\fcharset161\fprq2 Times New Roman Greek;}
{\f58\froman\fcharset162\fprq2 Times New Roman Tur;}{\f59\froman\fcharset177\fprq2 Times New Roman (Hebrew);}{\f60\froman\fcharset178\fprq2 Times New Roman (Arabic);}{\f61\froman\fcharset186\fprq2 Times New Roman Baltic;}
{\f62\fswiss\fcharset238\fprq2 Arial CE;}{\f63\fswiss\fcharset204\fprq2 Arial Cyr;}{\f65\fswiss\fcharset161\fprq2 Arial Greek;}{\f66\fswiss\fcharset162\fprq2 Arial Tur;}{\f67\fswiss\fcharset177\fprq2 Arial (Hebrew);}
{\f68\fswiss\fcharset178\fprq2 Arial (Arabic);}{\f69\fswiss\fcharset186\fprq2 Arial Baltic;}}{\colortbl;\red0\green0\blue0;\red0\green0\blue255;\red0\green255\blue255;\red0\green255\blue0;\red255\green0\blue255;\red255\green0\blue0;\red255\green255\blue0;
\red255\green255\blue255;\red0\green0\blue128;\red0\green128\blue128;\red0\green128\blue0;\red128\green0\blue128;\red128\green0\blue0;\red128\green128\blue0;\red128\green128\blue128;\red192\green192\blue192;}{\stylesheet{
\ql \li0\ri0\sb120\sa80\widctlpar\aspalpha\aspnum\faauto\adjustright\rin0\lin0\itap0 \fs24\lang2057\langfe1033\cgrid\langnp2057\langfenp1033 \snext0 Normal;}{
\s1\ql \li0\ri0\sb100\sa100\sbauto1\saauto1\widctlpar\aspalpha\aspnum\faauto\adjustright\rin0\lin0\itap0 \b\f7\fs32\lang1030\langfe1033\kerning36\cgrid\langnp1044\langfenp1033 \sbasedon0 \snext1 heading 1;}{
\s2\ql \li0\ri0\sb100\sa100\sbauto1\saauto1\widctlpar\aspalpha\aspnum\faauto\adjustright\rin0\lin0\itap0 \b\f7\fs28\lang1030\langfe1033\cgrid\langnp1044\langfenp1033 \sbasedon0 \snext2 heading 2;}{
\s3\ql \li0\ri0\sb240\sa60\keepn\widctlpar\aspalpha\aspnum\faauto\adjustright\rin0\lin0\itap0 \b\f1\fs26\lang2057\langfe1033\cgrid\langnp2057\langfenp1033 \sbasedon0 \snext0 heading 3;}{\*\cs10 \additive Default Paragraph Font;}}{\info
{\creatim\yr2002\mo1\dy20\hr8\min43}{\revtim\yr2002\mo1\dy20\hr13\min56}{\version4}{\edmins1}{\nofpages1}{\nofwords0}{\nofchars0}{\*\company Hiof}{\nofcharsws0}
{\vern8247}}\paperw11906\paperh16838\margl1417\margr1417\margt1417\margb1417 \widowctrl\ftnbj\aenddoc\noxlattoyen\expshrtn\noultrlspc\dntblnsbdb\nospaceforul\hyphcaps0\formshade\horzdoc\dgmargin\dghspace180\dgvspace180\dghorigin1417\dgvorigin1417\dghshow1
\dgvshow1\jexpand\viewkind1\viewscale100\pgbrdrhead\pgbrdrfoot\splytwnine\ftnlytwnine\htmautsp\nolnhtadjtbl\useltbaln\alntblind\lytcalctblwd\lyttblrtgr\lnbrkrule \fet0\sectd \linex0\headery708\footery708\colsx708\endnhere\sectlinegrid360\sectdefaultcl
{\*\pnseclvl1\pnucrm\pnstart1\pnindent720\pnhang{\pntxta .}}{\*\pnseclvl2\pnucltr\pnstart1\pnindent720\pnhang{\pntxta .}}{\*\pnseclvl3\pndec\pnstart1\pnindent720\pnhang{\pntxta .}}{\*\pnseclvl4\pnlcltr\pnstart1\pnindent720\pnhang{\pntxta )}}{\*\pnseclvl5
\pndec\pnstart1\pnindent720\pnhang{\pntxtb (}{\pntxta )}}{\*\pnseclvl6\pnlcltr\pnstart1\pnindent720\pnhang{\pntxtb (}{\pntxta )}}{\*\pnseclvl7\pnlcrm\pnstart1\pnindent720\pnhang{\pntxtb (}{\pntxta )}}{\*\pnseclvl8\pnlcltr\pnstart1\pnindent720\pnhang
{\pntxtb (}{\pntxta )}}{\*\pnseclvl9\pnlcrm\pnstart1\pnindent720\pnhang{\pntxtb (}{\pntxta )}}
</xsl:text>
<xsl:value-of select="h1|dtb:h1"/>
</xsl:template>

<xsl:template name="H1PSTART">
<xsl:text>\pard\plain \s1\ql \li0\ri0\sb100\sa100\sbauto1\saauto1\widctlpar\aspalpha\aspnum\faauto\outlinelevel0\adjustright\rin0\lin0\itap0
\b\f7\fs32\lang1030\langfe1033\kerning36\cgrid\langnp1044\langfenp1033
{</xsl:text>
</xsl:template>

<xsl:template name="H2PSTART">
<xsl:text>\pard\plain \s2\ql \li0\ri0\sb100\sa100\sbauto1\saauto1\widctlpar\aspalpha\aspnum\faauto\outlinelevel1\adjustright\rin0\lin0\itap0
\b\f7\fs28\lang1030\langfe1033\cgrid\langnp1044\langfenp1033
{</xsl:text>
</xsl:template>


<xsl:template name="H3PSTART">
<xsl:text>\pard\plain \s3\ql \li0\ri0\sb240\sa60\keepn\widctlpar\aspalpha\aspnum\faauto\outlinelevel2\adjustright\rin0\lin0\itap0
\b\f1\fs26\lang1030\langfe1033\cgrid\langnp2057\langfenp1033
{</xsl:text>
</xsl:template>

<xsl:template name="H4PSTART">
<xsl:text>\pard\plain \s4\sb240\sa60\keepn\widctlpar\outlinelevel3\adjustright \b\f1\lang1030\cgrid
{</xsl:text>
</xsl:template>

<xsl:template name="H5PSTART">
<xsl:text>\pard\plain \s5\sb240\sa60\widctlpar\outlinelevel4\adjustright \f1\fs22\lang1030\cgrid
{</xsl:text>
</xsl:template>

<xsl:template name="H6PSTART">
<xsl:text>\pard\plain \s6\sb240\sa60\widctlpar\outlinelevel5\adjustright \i\fs22\lang1030\cgrid
{</xsl:text>
</xsl:template>



<xsl:template name="PPSTART">
<xsl:text>
\pard\plain \ql \li0\ri0\sb120\sa80\widctlpar\aspalpha\aspnum\faauto\adjustright\rin0\lin0\itap0 \fs24\lang1030\langfe1033\cgrid\langnp2057\langfenp1033
{
</xsl:text>
</xsl:template>

<xsl:template name="PPSTARTSTRONG">
<xsl:text>
\b
</xsl:text>
</xsl:template>

<xsl:template name="PPSTOPSTRONG">
<xsl:text>
\b0
</xsl:text>
</xsl:template>


<xsl:template name="PPSTARTITALIC">
<xsl:text>
\i
</xsl:text>
</xsl:template>

<xsl:template name="PPSTOPITALIC">
<xsl:text>
\i0
</xsl:text>
</xsl:template>

<xsl:template name="PSTOP">
<xsl:text>
\par }
</xsl:text>
</xsl:template>

<xsl:template name="NEWLINE">
<xsl:text>\line </xsl:text>
</xsl:template>

<xsl:template name="PAGENUM">
<xsl:text>
\pard\plain \ql \li0\ri0\sb120\sa80\widctlpar\aspalpha\aspnum\faauto\adjustright\rin0\lin0\itap0 \fs16 \i\lang1030\langfe1033\cgrid\langnp2057\langfenp1033
{
</xsl:text>
</xsl:template>

<xsl:template name="TABLESTART">
<xsl:text>\trowd \trgaph70\trleft-70\trbrdrt
\brdrs\brdrw10 \trbrdrl\brdrs\brdrw10 \trbrdrb\brdrs\brdrw10 \trbrdrr\brdrs\brdrw10 \trbrdrh\brdrs\brdrw10 \trbrdrv\brdrs\brdrw10
</xsl:text>
</xsl:template>

<xsl:template name="CELLNUMBER">
<xsl:text>\clvertalt\clbrdrt\brdrs\brdrw10 \clbrdrl\brdrs\brdrw10 \clbrdrb\brdrs\brdrw10 \clbrdrr\brdrs\brdrw10 \cltxlrtb \cellx</xsl:text>
</xsl:template>

<xsl:template name="CELLSTOP">
<xsl:text>\pard\plain \widctlpar\intbl\adjustright \lang1030 \f1\fs22\cgrid {</xsl:text>
</xsl:template>



<xsl:template name="CELLINDHOLD">
<xsl:text>\cell </xsl:text>
</xsl:template>

<xsl:template name="TABLESTOP">
<xsl:text>} \pard \widctlpar\intbl\adjustright {\row }</xsl:text>
</xsl:template>

<xsl:template name="TABLEEND">
<xsl:text>\pard \widctlpar\adjustright {</xsl:text>
</xsl:template>

<xsl:template name="LIST">
<xsl:text>{\pntext\pard\plain\f3\fs22\lang1030\cgrid \loch\af3\dbch\af0\hich\f3 \'b7\tab}
\pard\plain \fi-360\li360\widctlpar\jclisttab\tx360{\*\pn \pnlvlblt\ilvl0\ls1\pnrnot0\pnf3\pnstart1\pnindent360\pnhang{\pntxtb \'b7}}\ls1\adjustright \f1\fs22\cgrid{</xsl:text>
</xsl:template>

<xsl:template name="OBJECTSTART">
<xsl:text>{\object\objemb\objw720\objh720{\*\objdata</xsl:text>
</xsl:template>


<xsl:template name="OBJECTSTOP">
<xsl:text>}}}\par</xsl:text>
</xsl:template>

<xsl:template name="BOGMARKSTART">
<xsl:text>\pard\plain \s15\sb240\sa120\widctlpar
\tqr\tx9062\adjustright \b\fs20\lang1030\cgrid{</xsl:text>
</xsl:template>



<xsl:template name="BOGMARKMID">
<xsl:text>}{\field{\*\fldinst {PAGEREF _Toc</xsl:text>
</xsl:template>

<xsl:template name="BOGMARKEND">
<xsl:text>\\h }</xsl:text>
</xsl:template>

<xsl:template name="BOGMARKSTART2">
<xsl:text>\pard\plain \adjustright \fs18\lang1030\cgrid{</xsl:text>
</xsl:template>


<xsl:template name="BOGMARKREFSTART">
<xsl:text>{\*\bkmkstart _Toc</xsl:text>
</xsl:template>

<xsl:template name="BOGMARKREFSTARTEND">
<xsl:text>}</xsl:text>
</xsl:template>

<xsl:template name="BOGMARKREFEND">
<xsl:text>{\*\bkmkend _Toc</xsl:text>
</xsl:template>

<xsl:template name="BOGMARKREFENDEND">
<xsl:text>}</xsl:text>
</xsl:template>

<!--
RTF is a 8-bit format. That would limit it to ASCII, but RTF can encode characters 
beyond ASCII by escape sequences. The character escapes are of two types: code page 
escapes and Unicode escapes. In a code page escape, two hexadecimal digits following 
an apostrophe are used for denoting a character taken from a Windows code page. 

For example, if control codes specifying Windows-1256 are present, the sequence 
\'c8 will encode the Arabic letter beh (ب).

If a Unicode escape is required, the control word \u is used, followed by a 16-bit 
signed decimal integer giving the Unicode codepoint number. For the benefit of programs 
without Unicode support, this must be followed by the nearest representation of this 
character in the specified code page. For example, \u1576? would give the Arabic letter beh, 
specifying that older programs which do not have Unicode support should render it as a 
question mark instead.

The control word \uc0 can be used to indicate that subsequent Unicode escape sequences 
within the current group do not specify a substitution character.
-->

<xsl:variable name="latin1">&#160;&#161;&#162;&#163;&#164;&#165;&#166;&#167;&#168;&#169;&#170;&#171;&#172;&#173;&#174;&#175;&#176;&#177;&#178;&#179;&#180;&#181;&#182;&#183;&#184;&#185;&#186;&#187;&#188;&#189;&#190;&#191;&#192;&#193;&#194;&#195;&#196;&#197;&#198;&#199;&#200;&#201;&#202;&#203;&#204;&#205;&#206;&#207;&#208;&#209;&#210;&#211;&#212;&#213;&#214;&#215;&#216;&#217;&#218;&#219;&#220;&#221;&#222;&#223;&#224;&#225;&#226;&#227;&#228;&#229;&#230;&#231;&#232;&#233;&#234;&#235;&#236;&#237;&#238;&#239;&#240;&#241;&#242;&#243;&#244;&#245;&#246;&#247;&#248;&#249;&#250;&#251;&#252;&#253;&#254;&#255;&#256;&#257;&#258;&#259;&#260;&#261;&#262;&#263;&#264;&#265;&#266;&#267;&#268;&#269;&#270;&#271;&#272;&#273;&#274;&#275;&#276;&#277;&#278;&#279;&#280;&#281;&#282;&#283;&#284;&#285;&#286;&#287;&#288;&#289;&#290;&#291;&#292;&#293;&#294;&#295;&#296;&#297;&#298;&#299;&#300;&#301;&#302;&#303;&#304;&#305;&#306;&#307;&#308;&#309;&#310;&#311;&#312;&#313;&#314;&#315;&#316;&#317;&#318;&#319;&#320;&#321;&#322;&#323;&#324;&#325;&#326;&#327;&#328;&#329;&#330;&#331;&#332;&#333;&#334;&#335;&#336;&#337;&#338;&#339;&#340;&#341;&#342;&#343;&#344;&#345;&#346;&#347;&#348;&#349;&#350;&#351;&#352;&#353;&#354;&#355;&#356;&#357;&#358;&#359;&#360;&#361;&#362;&#363;&#364;&#365;&#366;&#367;&#368;&#369;&#370;&#371;&#372;&#373;&#374;&#375;&#376;&#377;&#378;&#379;&#380;&#381;&#382;&#383;&#384;&#385;&#386;&#387;&#388;&#389;&#390;&#391;&#392;&#393;&#394;&#395;&#396;&#397;&#398;&#399;&#400;&#401;&#402;&#403;&#404;&#405;&#406;&#407;&#408;&#409;&#410;&#411;&#412;&#413;&#414;&#415;&#416;&#417;&#418;&#419;&#420;&#421;&#422;&#423;&#424;&#425;&#426;&#427;&#428;&#429;&#430;&#431;&#432;&#433;&#434;&#435;&#436;&#437;&#438;&#439;&#440;&#441;&#442;&#443;&#444;&#445;&#446;&#447;&#448;&#449;&#450;&#451;&#452;&#453;&#454;&#455;&#456;&#457;&#458;&#459;&#460;&#461;&#462;&#463;&#464;&#465;&#466;&#467;&#468;&#469;&#470;&#471;&#472;&#473;&#474;&#475;&#476;&#477;&#478;&#479;&#480;&#481;&#482;&#483;&#484;&#485;&#486;&#487;&#488;&#489;&#490;&#491;&#492;&#493;&#494;&#495;&#496;&#497;&#498;&#499;&#500;&#501;&#502;&#503;&#504;&#505;&#506;&#507;&#508;&#509;&#510;&#511;&#512;&#513;&#514;&#515;&#516;&#517;&#518;&#519;&#520;&#521;&#522;&#523;&#524;&#525;&#526;&#527;&#528;&#529;&#530;&#531;&#532;&#533;&#534;&#535;&#536;&#537;&#538;&#539;&#540;&#541;&#542;&#543;&#544;&#545;&#546;&#547;&#548;&#549;&#550;&#551;&#552;&#553;&#554;&#555;&#556;&#557;&#558;&#559;&#560;&#561;&#562;&#563;&#564;&#565;&#566;&#567;&#568;&#569;&#570;&#571;&#572;&#573;&#574;&#575;&#576;&#577;&#578;&#579;&#580;&#581;&#582;&#583;&#584;&#585;&#586;&#587;&#588;&#589;&#590;&#591;&#592;&#593;&#594;&#595;&#596;&#597;&#598;&#599;&#600;&#601;&#602;&#603;&#604;&#605;&#606;&#607;&#608;&#609;&#610;&#611;&#612;&#613;&#614;&#615;&#616;&#617;&#618;&#619;&#620;&#621;&#622;&#623;&#624;&#625;&#626;&#627;&#628;&#629;&#630;&#631;&#632;&#633;&#634;&#635;&#636;&#637;&#638;&#639;&#640;&#641;&#642;&#643;&#644;&#645;&#646;&#647;&#648;&#649;&#650;&#651;&#652;&#653;&#654;&#655;&#656;&#657;&#658;&#659;&#660;&#661;&#662;&#663;&#664;&#665;&#666;&#667;&#668;&#669;&#670;&#671;&#672;&#673;&#674;&#675;&#676;&#677;&#678;&#679;&#680;&#681;&#682;&#683;&#684;&#685;&#686;&#687;&#688;&#689;&#690;&#691;&#692;&#693;&#694;&#695;&#696;&#697;&#698;&#699;&#700;&#701;&#702;&#703;&#704;&#705;&#706;&#707;&#708;&#709;&#710;&#711;&#712;&#713;&#714;&#715;&#716;&#717;&#718;&#719;&#720;&#721;&#722;&#723;&#724;&#725;&#726;&#727;&#728;&#729;&#730;&#731;&#732;&#733;&#734;&#735;&#736;&#737;&#738;&#739;&#740;&#741;&#742;&#743;&#744;&#745;&#746;&#747;&#748;&#749;&#750;&#751;&#752;&#753;&#754;&#755;&#756;&#757;&#758;&#759;&#760;&#761;&#762;&#763;&#764;&#765;&#766;&#767;&#768;&#769;&#770;&#771;&#772;&#773;&#774;&#775;&#776;&#777;&#778;&#779;&#780;&#781;&#782;&#783;&#784;&#785;&#786;&#787;&#788;&#789;&#790;&#791;&#792;&#793;&#794;&#795;&#796;&#797;&#798;&#799;&#800;&#801;&#802;&#803;&#804;&#805;&#806;&#807;&#808;&#809;&#810;&#811;&#812;&#813;&#814;&#815;&#816;&#817;&#818;&#819;&#820;&#821;&#822;&#823;&#824;&#825;&#826;&#827;&#828;&#829;&#830;&#831;&#832;&#833;&#834;&#835;&#836;&#837;&#838;&#839;&#840;&#841;&#842;&#843;&#844;&#845;&#846;&#847;&#848;&#849;&#850;&#851;&#852;&#853;&#854;&#855;&#856;&#857;&#858;&#859;&#860;&#861;&#862;&#863;&#864;&#865;&#866;&#867;&#868;&#869;&#870;&#871;&#872;&#873;&#874;&#875;&#876;&#877;&#878;&#879;&#880;&#881;&#882;&#883;&#884;&#885;&#886;&#887;&#888;&#889;&#890;&#891;&#892;&#893;&#894;&#895;&#896;&#897;&#898;&#899;&#900;&#901;&#902;&#903;&#904;&#905;&#906;&#907;&#908;&#909;&#910;&#911;&#912;&#913;&#914;&#915;&#916;&#917;&#918;&#919;&#920;&#921;&#922;&#923;&#924;&#925;&#926;&#927;&#928;&#929;&#930;&#931;&#932;&#933;&#934;&#935;&#936;&#937;&#938;&#939;&#940;&#941;&#942;&#943;&#944;&#945;&#946;&#947;&#948;&#949;&#950;&#951;&#952;&#953;&#954;&#955;&#956;&#957;&#958;&#959;&#960;&#961;&#962;&#963;&#964;&#965;&#966;&#967;&#968;&#969;&#970;&#971;&#972;&#973;&#974;&#975;&#976;&#977;&#978;&#979;&#980;&#981;&#982;&#983;&#984;&#985;&#986;&#987;&#988;&#989;&#990;&#991;&#992;&#993;&#994;&#995;&#996;&#997;&#998;&#999;&#1000;&#1001;&#1002;&#1003;&#1004;&#1005;&#1006;&#1007;&#1008;&#1009;&#1010;&#1011;&#1012;&#1013;&#1014;&#1015;&#1016;&#1017;&#1018;&#1019;&#1020;&#1021;&#1022;&#1023;&#1024;&#1025;&#1026;&#1027;&#1028;&#1029;&#1030;&#1031;&#1032;&#1033;&#1034;&#1035;&#1036;&#1037;&#1038;&#1039;&#1040;&#1041;&#1042;&#1043;&#1044;&#1045;&#1046;&#1047;&#1048;&#1049;&#1050;&#1051;&#1052;&#1053;&#1054;&#1055;&#1056;&#1057;&#1058;&#1059;&#1060;&#1061;&#1062;&#1063;&#1064;&#1065;&#1066;&#1067;&#1068;&#1069;&#1070;&#1071;&#1072;&#1073;&#1074;&#1075;&#1076;&#1077;&#1078;&#1079;&#1080;&#1081;&#1082;&#1083;&#1084;&#1085;&#1086;&#1087;&#1088;&#1089;&#1090;&#1091;&#1092;&#1093;&#1094;&#1095;&#1096;&#1097;&#1098;&#1099;&#1100;&#1101;&#1102;&#1103;&#1104;&#1105;&#1106;&#1107;&#1108;&#1109;&#1110;&#1111;&#1112;&#1113;&#1114;&#1115;&#1116;&#1117;&#1118;&#1119;&#1120;&#1121;&#1122;&#1123;&#1124;&#1125;&#1126;&#1127;&#1128;&#1129;&#1130;&#1131;&#1132;&#1133;&#1134;&#1135;&#1136;&#1137;&#1138;&#1139;&#1140;&#1141;&#1142;&#1143;&#1144;&#1145;&#1146;&#1147;&#1148;&#1149;&#1150;&#1151;&#1152;&#1153;&#1154;&#1155;&#1156;&#1157;&#1158;&#1159;&#1160;&#1161;&#1162;&#1163;&#1164;&#1165;&#1166;&#1167;&#1168;&#1169;&#1170;&#1171;&#1172;&#1173;&#1174;&#1175;&#1176;&#1177;&#1178;&#1179;&#1180;&#1181;&#1182;&#1183;&#1184;&#1185;&#1186;&#1187;&#1188;&#1189;&#1190;&#1191;&#1192;&#1193;&#1194;&#1195;&#1196;&#1197;&#1198;&#1199;&#1200;&#1201;&#1202;&#1203;&#1204;&#1205;&#1206;&#1207;&#1208;&#1209;&#1210;&#1211;&#1212;&#1213;&#1214;&#1215;&#1216;&#1217;&#1218;&#1219;&#1220;&#1221;&#1222;&#1223;&#1224;&#1225;&#1226;&#1227;&#1228;&#1229;&#1230;&#1231;&#1232;&#1233;&#1234;&#1235;&#1236;&#1237;&#1238;&#1239;&#1240;&#1241;&#1242;&#1243;&#1244;&#1245;&#1246;&#1247;&#1248;&#1249;&#1250;&#1251;&#1252;&#1253;&#1254;&#1255;&#1256;&#1257;&#1258;&#1259;&#1260;&#1261;&#1262;&#1263;&#1264;&#1265;&#1266;&#1267;&#1268;&#1269;&#1270;&#1271;&#1272;&#1273;&#1274;&#1275;&#1276;&#1277;&#1278;</xsl:variable>
<xsl:template name="rtf-encode">
	<xsl:param name="str"/>
	<xsl:if test="$str">
		<xsl:variable name="first-char" select="substring($str,1,1)"/>
		<xsl:choose>
			<xsl:when test="$first-char = '\'">\\</xsl:when>
			<xsl:when test="$first-char = '{'">\{</xsl:when>
			<xsl:when test="$first-char = '}'">\}</xsl:when>
			<xsl:when test="not(contains($latin1,$first-char))">
				<xsl:value-of select="$first-char"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:variable name="codepoint" select="string-length(substring-before($latin1,$first-char)) + 160" />
				<xsl:value-of select="concat('\u',$codepoint,'?')"/>
			</xsl:otherwise>
		</xsl:choose>
		<xsl:if test="string-length($str) &gt; 1">
			<xsl:call-template name="rtf-encode">
				<xsl:with-param name="str" select="substring($str,2)"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:if>
</xsl:template>
	<!-- =========================================================================
dtbook
	============================================================================== -->
	<xsl:template match="dtbook|dtb:dtbook">		
		<xsl:apply-templates/>
	</xsl:template>	
	<!-- =========================================================================
head
	============================================================================== -->	
	<xsl:template match="head|dtb:head">		
		<xsl:apply-templates/>	
	</xsl:template>	
	<!-- =========================================================================
title
	============================================================================== -->	
	<xsl:template match="title|dtb:title">		
		<xsl:apply-templates/>	
	</xsl:template>	
	<!-- =========================================================================
link
	============================================================================== -->	
	<xsl:template match="link|dtb:link">			
		<xsl:apply-templates/>	
	</xsl:template>	
	<!-- =========================================================================
meta
	============================================================================== -->	
	<xsl:template match="meta|dtb:meta">		
		<xsl:apply-templates/>	
	</xsl:template>	
	<!-- =========================================================================
style
	============================================================================== -->	
	<xsl:template match="style|dtb:style">		
		<xsl:apply-templates/>	
	</xsl:template>	
	<!-- =========================================================================
book
	============================================================================== -->	
	<xsl:template match="book|dtb:book">		
		<xsl:apply-templates/>	
	</xsl:template>	
	<!-- =========================================================================
level
	============================================================================== -->	
	<xsl:template match="level|dtb:level">		
		<xsl:apply-templates/>	
	</xsl:template>	
	<!-- =========================================================================
linenum
	============================================================================== -->	
	<xsl:template match="linenum|dtb:linenum">	
		<xsl:call-template name="PPSTART"/>			
		<xsl:apply-templates/>			
		<xsl:text> </xsl:text>				
	</xsl:template>	
	<!-- =========================================================================
adress
	============================================================================== -->	
	<xsl:template match="address|dtb:address">		
		<xsl:apply-templates/>	
	</xsl:template>	
	<!-- =========================================================================
author
	============================================================================== -->	
	<xsl:template match="author|dtb:author">		
		<xsl:apply-templates/>	
	</xsl:template>	
	<!-- =========================================================================
notice
	============================================================================== -->	
	<xsl:template match="notice|dtb:notice">	
		<xsl:call-template name="PPSTART"/>			
		<xsl:apply-templates/>			
		<xsl:call-template name="PSTOP"/>	
	</xsl:template>	
	<!-- =========================================================================
note
	============================================================================== -->	
	<xsl:template match="note|dtb:note">		
		<xsl:apply-templates/>	
	</xsl:template>	
	<!-- =========================================================================
annotation
	============================================================================== -->	
	<xsl:template match="annotation|dtb:annotation">		
		<xsl:apply-templates/>	
	</xsl:template>	
	<!-- =========================================================================
line
	============================================================================== -->	
	<xsl:template match="line|dtb:line">	
	    <xsl:call-template name="PPSTART"/>
		<xsl:apply-templates/>				
		<xsl:call-template name="PSTOP"/>	
	</xsl:template>	
	<!-- =========================================================================
a
	============================================================================== -->	
	<xsl:template match="a|dtb:a">	
		<xsl:apply-templates/>	
	</xsl:template>	
	<!-- =========================================================================
strong
	============================================================================== -->	
	<xsl:template match="strong|dtb:strong">					
		<xsl:call-template name="PPSTARTSTRONG"/>			
		<xsl:apply-templates/>
		<xsl:call-template name="PPSTOPSTRONG"/>
	</xsl:template>	
	<!-- =========================================================================
em
	============================================================================== -->	
	<xsl:template match="em|dtb:em">
		<xsl:call-template name="PPSTARTITALIC"/>			
		<xsl:apply-templates/>
		<xsl:call-template name="PPSTOPITALIC"/>
	</xsl:template>	
	<!-- =========================================================================
dfn
	============================================================================== -->	
	<xsl:template match="dfn|dtb:dfn">		
		<xsl:apply-templates/>	
	</xsl:template>	
	<!-- =========================================================================
kbd
	============================================================================== -->	
	<xsl:template match="kbd|dtb:kbd">		
		<xsl:apply-templates/>	
	</xsl:template>	
	<!-- =========================================================================
code
	============================================================================== -->	
	<xsl:template match="code|dtb:code">		
		<xsl:apply-templates/>	
	</xsl:template>	
	<!-- =========================================================================
samp
	============================================================================== -->	
	<xsl:template match="samp|dtb:samp">		
		<xsl:apply-templates/>	
	</xsl:template>	
	<!-- =========================================================================
cite
	============================================================================== -->	
	<xsl:template match="cite|dtb:cite">		
		<xsl:apply-templates/>	
	</xsl:template>	
	<!-- =========================================================================
abbr
	============================================================================== -->	
	<xsl:template match="abbr|dtb:abbr">		
		<xsl:apply-templates/>	
	</xsl:template>	
	<!-- =========================================================================
acronym
	============================================================================== -->	
	<xsl:template match="acronym|dtb:acronym">		
		<xsl:apply-templates/>	
	</xsl:template>	
	<!-- =========================================================================
sub
	============================================================================== -->	
	<xsl:template match="sub|dtb:sub">	
		<xsl:apply-templates/>	
	</xsl:template>	
	<!-- =========================================================================
sup
	============================================================================== -->	
	<xsl:template match="sup|dtb:sup">		
		<xsl:apply-templates/>	
	</xsl:template>	
	<!-- =========================================================================
span
	============================================================================== -->	
	<xsl:template match="span|dtb:span">		
		<xsl:apply-templates/>	
	</xsl:template>	
	<!-- =========================================================================
bdo
	============================================================================== -->	
	<xsl:template match="bdo|dtb:bdo">			
		<xsl:apply-templates/>	
	</xsl:template>	
	<!-- =========================================================================
sent
	============================================================================== -->	
	<xsl:template match="sent|dtb:sent">		
		<xsl:apply-templates/>	
	</xsl:template>	
	<!-- =========================================================================
w
	============================================================================== -->	
	<xsl:template match="w|dtb:w">		
		<xsl:apply-templates/>	
	</xsl:template>	
	<!-- =========================================================================
noteref
	============================================================================== -->	
	<xsl:template match="noteref|dtb:noteref">		
		<xsl:call-template name="PPSTART"/>			
		<xsl:apply-templates/>
		<xsl:call-template name="PSTOP"/>
	</xsl:template>	
	<!-- =========================================================================
annoref
	============================================================================== -->	
	<xsl:template match="annoref|dtb:annoref">		
		<xsl:apply-templates/>	
	</xsl:template>	
	<!-- =========================================================================
q
	============================================================================== -->	
	<xsl:template match="q|dtb:q">		
		<xsl:apply-templates/>	
	</xsl:template>	
	<!-- =========================================================================
img
	============================================================================== -->	
	<xsl:template match="img|dtb:img">
	<!--<xsl:call-template name="OBJECTSTART"/>	-->
		<xsl:apply-templates/>	
	<!--<xsl:call-template name="OBJECTSTOP"/> -->
	</xsl:template>	
	<!-- =========================================================================
imggroup
	============================================================================== -->	
	<xsl:template match="imggroup|dtb:imggroup">	
		<xsl:apply-templates/>	
	</xsl:template>	
	<!-- =========================================================================
hr
	============================================================================== -->	
	<xsl:template match="hr|dtb:hr">		
		<xsl:apply-templates/>	
	</xsl:template>	
	<!-- =========================================================================
levelhd
	============================================================================== -->	
	<xsl:template match="levelhd|dtb:levelhd">	
		<xsl:choose>	
			<xsl:when test="@depth='1'">		
				<xsl:call-template name="H1PSTART"/>	
				<xsl:call-template name="BOGMARKREFSTART"/>	
				<xsl:value-of select="count(preceding::level)+300"/>	
				<xsl:call-template name="BOGMARKREFSTARTEND"/>	
				<xsl:apply-templates/>	
				<xsl:call-template name="BOGMARKREFEND"/>	
				<xsl:value-of select="count(preceding::level)+300"/>	
				<xsl:call-template name="BOGMARKREFENDEND"/>	
				<xsl:call-template name="PSTOP"/>	
			</xsl:when>	
			<xsl:when test="@depth='2'">	
				<xsl:call-template name="H2PSTART"/>	
				<xsl:call-template name="BOGMARKREFSTART"/>	
				<xsl:value-of select="count(preceding::level)+2000000"/>	
				<xsl:call-template name="BOGMARKREFSTARTEND"/>
				<xsl:apply-templates/>	
				<xsl:call-template name="BOGMARKREFEND"/>	
				<xsl:value-of select="count(preceding::level)+2000000"/>	
				<xsl:call-template name="BOGMARKREFENDEND"/>	
				<xsl:call-template name="PSTOP"/>	
			</xsl:when>
			<xsl:when test="@depth='3'">	
				<xsl:call-template name="H3PSTART"/>
				<xsl:apply-templates/>				
				<xsl:call-template name="PSTOP"/>
			</xsl:when>
			<xsl:when test="@depth='4'">	
				<xsl:call-template name="H4PSTART"/>
				<xsl:apply-templates/>
				<xsl:call-template name="PSTOP"/>
			</xsl:when>
			<xsl:when test="@depth='5'">	
				<xsl:call-template name="H5PSTART"/>
				<xsl:apply-templates/>
				<xsl:call-template name="PSTOP"/>
			</xsl:when>
			<xsl:when test="@depth='6'">	
				<xsl:call-template name="H6PSTART"/>
				<xsl:apply-templates/>
				<xsl:call-template name="PSTOP"/>
			</xsl:when>
		</xsl:choose>	
	</xsl:template>	
	<!-- =========================================================================
h1
	============================================================================== -->	
	<xsl:template match="h1|dtb:h1">	
		<xsl:call-template name="H1PSTART"/>	
		<xsl:call-template name="BOGMARKREFSTART"/>	
		<xsl:value-of select="count(preceding::level1)"/>	
		<xsl:call-template name="BOGMARKREFSTARTEND"/>
		<xsl:apply-templates/>	
		<xsl:call-template name="BOGMARKREFEND"/>	
		<xsl:value-of select="count(preceding::level1)"/>	
		<xsl:call-template name="BOGMARKREFENDEND"/>	
		<xsl:call-template name="PSTOP"/>	
	</xsl:template>	
	<!-- =========================================================================
h2
	============================================================================== -->	
	<xsl:template match="h2|dtb:h2">	
		<xsl:call-template name="H2PSTART"/>	
		<xsl:call-template name="BOGMARKREFSTART"/>	
		<xsl:value-of select="count(preceding::level2)+1000000"/>	
		<xsl:call-template name="BOGMARKREFSTARTEND"/>
		<xsl:apply-templates/>	
		<xsl:call-template name="BOGMARKREFEND"/>	
		<xsl:value-of select="count(preceding::level2)+1000000"/>	
		<xsl:call-template name="BOGMARKREFENDEND"/>	
		<xsl:call-template name="PSTOP"/>
	</xsl:template>	
	<!-- =========================================================================
h3
	============================================================================== -->	
	<xsl:template match="h3|dtb:h3">	
		<xsl:call-template name="H3PSTART"/>
		<xsl:apply-templates/>		
		<xsl:call-template name="PSTOP"/>
	</xsl:template>	
	<!-- =========================================================================
h4
	============================================================================== -->	
	<xsl:template match="h4|dtb:h4">	
		<xsl:call-template name="H4PSTART"/>
		<xsl:apply-templates/>		
		<xsl:call-template name="PSTOP"/>
	</xsl:template>	
	<!-- =========================================================================
h5
	============================================================================== -->	
	<xsl:template match="h5|dtb:h5">	
		<xsl:call-template name="H5PSTART"/>	
		<xsl:apply-templates/>		
		<xsl:call-template name="PSTOP"/>
	</xsl:template>	
	<!-- =========================================================================
h6
	============================================================================== -->	
	<xsl:template match="h6|dtb:h6">	
		<xsl:call-template name="H6PSTART"/>	
		<xsl:apply-templates/>		
		<xsl:call-template name="PSTOP"/>
	</xsl:template>	
	<!-- =========================================================================
blockqoute
	============================================================================== -->	
	<xsl:template match="blockquote|dtb:blockquote">		
		<xsl:apply-templates/>	
	</xsl:template>	
	<!-- =========================================================================
dl
	============================================================================== -->	
	<xsl:template match="dl|dtb:dl">		
		<xsl:call-template name="PPSTART"/>			
		<xsl:apply-templates/>					
		<xsl:call-template name="PSTOP"/>	
	</xsl:template>	
	<!-- =========================================================================
dt
	============================================================================== -->	
	<xsl:template match="dt|dtb:dt">		
		<xsl:apply-templates/>	
	</xsl:template>	
	<!-- =========================================================================
dd
	============================================================================== -->	
	<xsl:template match="dd|dtb:dd">		
		<xsl:apply-templates/>	
	</xsl:template>	
	<!-- =========================================================================
list
	============================================================================== -->	
	<xsl:template match="list|dtb:list">
		<xsl:call-template name="NEWLINE"/>
		<xsl:for-each select="li|dtb:li">
			<xsl:call-template name="LIST"/>	
			<xsl:apply-templates/>	
			<xsl:call-template name="PSTOP"/>						
		</xsl:for-each>						
		<xsl:call-template name="NEWLINE"/>
	</xsl:template>	
	<!-- =========================================================================
li
	============================================================================== -->	
	<xsl:template match="li|dtb:li">	
		<xsl:apply-templates/>	
	</xsl:template>	
	<!-- =========================================================================
lic
	============================================================================== -->	
	<xsl:template match="lic|dtb:lic">		
		<xsl:apply-templates/>	
	</xsl:template>	
	<!-- =========================================================================
doctitle
	============================================================================== -->	
	<xsl:template match="doctitle|dtb:doctitle">		
	    <xsl:call-template name="H1PSTART"/>
			<xsl:apply-templates/>
		<xsl:call-template name="PSTOP"/>	
	</xsl:template>	
	<!-- =========================================================================
docauthor
	============================================================================== -->	
	<xsl:template match="docauthor|dtb:docauthor">
		<xsl:call-template name="H2PSTART"/>		
			<xsl:apply-templates/>	
		<xsl:call-template name="PSTOP"/>	
	</xsl:template>		
	<!-- =========================================================================
br
	============================================================================== -->	
	<xsl:template match="br|dtb:br">		
		<xsl:call-template name="NEWLINE"/>
		<xsl:apply-templates/>	
	</xsl:template>	
	<!-- =========================================================================
table
	============================================================================== -->	
	<xsl:template match="table|dtb:table">
		<xsl:choose>
			<xsl:when test="child::tbody | child::dtb:tbody">	
				<xsl:for-each select="tbody|dtb:tbody">
					<xsl:for-each select="tr|dtb:tr">
						<xsl:call-template name="TR"/>
					</xsl:for-each>
				</xsl:for-each>
			</xsl:when>			
			<xsl:otherwise>
				<xsl:for-each select="tr|dtb:tr">
					<xsl:call-template name="TR"/>
				</xsl:for-each>
			</xsl:otherwise>
		</xsl:choose>
		<xsl:call-template name="TABLEEND"/>	
		<xsl:call-template name="PSTOP"/>
	</xsl:template>	
	
	<xsl:template name="TR">
		<xsl:variable name="tables" select="9000 div count(td | th | dtb:td |dtb:th)"/>
		<xsl:call-template name="TABLESTART"/>			
		<xsl:for-each select="td | th | dtb:td | dtb:th">			
			<xsl:call-template name="CELLNUMBER"/>			
			<xsl:value-of select="$tables*position()"/>			
		</xsl:for-each>			
		<xsl:call-template name="CELLSTOP"/>			
		<xsl:for-each select="td | th | dtb:td | dtb:th ">			
			<xsl:apply-templates/>				
			<xsl:call-template name="CELLINDHOLD"/>					
		</xsl:for-each>	
		<xsl:call-template name="TABLESTOP"/>
	</xsl:template>
	<!-- =========================================================================
tbody
	============================================================================== -->	
	<xsl:template match="tbody|dtb:tbody">		
		<xsl:apply-templates/>	
	</xsl:template>	
	<!-- =========================================================================
tr
============================================================================== -->	
	<xsl:template match="tr|dtb:tr">	
	
		<xsl:apply-templates/>	
		
	</xsl:template>	
	<!-- =========================================================================
td
============================================================================== -->	
	<xsl:template match="td|dtb:td">		
		<xsl:apply-templates/>			
	</xsl:template>	
	<!-- =========================================================================
caption
	============================================================================== -->	
	<xsl:template match="caption|dtb:caption">	
	    <xsl:call-template name="PPSTARTITALIC"/>	    
		<xsl:apply-templates/>
		<xsl:call-template name="PPSTOPITALIC"/>	
		<xsl:call-template name="NEWLINE"/>
	</xsl:template>	
	<!-- =========================================================================
thead
	============================================================================== -->	
	<xsl:template match="thead|dtb:thead">		
		<xsl:apply-templates/>	
	</xsl:template>	
	<!-- =========================================================================
tfoot
	============================================================================== -->	
	<xsl:template match="tfoot|dtb:tfoot">		
		<xsl:apply-templates/>	
	</xsl:template>	
	<!-- =========================================================================
colgroup
	============================================================================== -->	
	<xsl:template match="colgroup|dtb:colgroup">	
		<xsl:apply-templates/>	
	</xsl:template>	
	<!-- =========================================================================
col
	============================================================================== -->	
	<xsl:template match="col|dtb:col">		
		<xsl:apply-templates/>	
	</xsl:template>	
	<!-- =========================================================================
th
	============================================================================== -->	
	<xsl:template match="th|dtb:th">	
		<xsl:apply-templates/>	
	</xsl:template>	
	<!-- =========================================================================
p
	============================================================================== -->	
	<xsl:template match="p|dtb:p">
		<xsl:call-template name="PPSTART"/>			
		<xsl:apply-templates/>			
		<xsl:call-template name="PSTOP"/>
	</xsl:template>	
	<!-- =========================================================================
prodnote
	============================================================================== -->	
	<xsl:template match="prodnote|dtb:prodnote">		
		<xsl:apply-templates/>	
	</xsl:template>	
	<!-- =========================================================================
sidebar
	============================================================================== -->	
	<xsl:template match="sidebar|dtb:sidebar">
		<xsl:apply-templates/>	
	</xsl:template>	
	<!-- =========================================================================
div
	============================================================================== -->	
	<xsl:template match="div|dtb:div">	
		<xsl:apply-templates/>	
	</xsl:template>	
	<!-- =========================================================================
pagenum
	============================================================================== -->	
	<xsl:template match="pagenum|dtb:pagenum">
		<xsl:call-template name="PAGENUM"/>			
		<xsl:apply-templates/>
		<xsl:call-template name="PSTOP"/>
	</xsl:template>
	<!-- =========================================================================
text
	============================================================================== -->	
	<xsl:template match="text()">
		<xsl:choose>	
			<xsl:when test="parent::title | parent::dtb:title">	</xsl:when>	
			<xsl:when test="parent::th | parent::dtb:th">
				<xsl:call-template name="PPSTARTSTRONG"/>
				<xsl:call-template name="rtf-encode">
					<xsl:with-param name="str" select="."/>
				</xsl:call-template>
				<xsl:call-template name="PPSTOPSTRONG"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="rtf-encode">
					<xsl:with-param name="str" select="."/>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

</xsl:stylesheet>
