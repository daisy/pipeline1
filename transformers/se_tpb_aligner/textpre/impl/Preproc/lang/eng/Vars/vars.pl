$phoneme_alphabet = "cmu" if $lang eq "eng";
$phoneme_alphabet = "tpa" if $lang eq "swe";


#***************************************************************#
#	Formats							#
#***************************************************************#
# For expansion of parentheses, bracket a.s. o:
# At least this number of words/tokens must exist between.
$expand_limit			=	3;
$parenthesis_pause_before	=	250;
$parenthesis_pause_after	=	250;
$parenthesis_pause		=	400;
$short_pause			=	300;


#***************************************************************#
# Acronym formats
#***************************************************************#
$acronym_endings = "s|er|ers|en|ens|et|ets|erna|ernas|n|ns|t|ts|an|ans|ar|ars|arna|arnas|or|ors|orna|ornas";

# Orthography - vowels and consonants
$vowels_ort = "[aouåeiyäö]";
$consonants_ort = "[bcdfghjklmnpqrstvwxz]";

# Transcription - vowels and consonants
$vowels_trk		=	"[aouåeiyäöë][un234:\.]*";
$consonants_trk		=	"r?[dtnsl][\:\.]?|[bfghjkmprvwz][\:\.]?|(?:ng|sj3?|tj3?|j3|rs3|dh|th|r3|r4)[\:\.]?";
$phonemes 		=	$vowels_trk . "|" . $consonants_trk;

#***************************************************************#
# Currency formats
#***************************************************************#
$currency_list	=	"(?:[\$\£\€\¥\¢]|dollars|dollar|pounds|pound|euro|cents|cent|pence)";

#***************************************************************#
# Unit formats
#***************************************************************#
# Numerals with letters
$num_words	=	"one|two|three|four|five|six|seven|eight|nine|ten|eleven|twelve|thirteen|fourteen|fifteen|sixteen|seventeen|eighteen|nineteen|twenty|thirty|fourty|fifty|sixty|seventy|eighty|ninety|hundred|thousand|hundreds|thousands";

#***************************************************************#
# Filename extensions
#***************************************************************#
$filename_extensions	=	"\.([a-z]{1..5})";
#***************************************************************#
# Decimal formats
#***************************************************************#
$fractions		=	"(?:[\½\¼\¾])";

#***************************************************************#
# Ordinal formats
#***************************************************************#
$ordinals_endings	=	"(?:st|nd|th)";
$ordinal_words		=	"(?:chapt?\.?|\§|part|chapter|section|paragraph)";

#***************************************************************#
# Roman numbers formats
#***************************************************************#
$roman_words	=	"(?:part|section|chapter|page|header|reference)";
$safe_roman	=	"(II|III|IV|V|VII|VIII|IX|X+I+|X+I+V+|X+V+|X+V+I+)";

#***************************************************************#
# Phone formats
#***************************************************************#
$phone_num		=	"(?:text|txt|order)?(?:telefon|tel\.?|(?:order)?fax\.?|tfn\.?)(?:nr\.?|nummer)?";
#***************************************************************#
# Time formats
#***************************************************************#
$hour_num		=	"(?:[01][0-9]|[0-9]|2[0-4])";
$minute_num		=	"[0-5][0-9]";
$klockan		=	"(?:klockan|kl\.?)";
#***************************************************************#
# Date formats
#***************************************************************#
# 1-9	10-19	20-29	30-31
$date_digit_format	=	"(?:[1-9]|1[0-9]|2[0-9]|3[01])";

$date_digit_31		=	"(?:[1-9]|[12][0-9]|3[01])";
$date_digit_30		=	"(?:[1-9]|[12][0-9]|30)";
$date_digit_29		=	"(?:[1-9]|[12][0-9])";

# Months with letters, full form and abbreviations
$month_letter_format	=	"(?:January|February|March|April|May|June|July|August|September|October|November|December|Jan\.|Feb\.|Mar\.|Jun\.|Jul\.|Aug\.|Sept?\.|Okt\.|Nov\.|Dec\.)";

# 1-9	10-12
$month_digit_format	=	"(?:[1-9]|1[0-2])";

# January, March, May, July, August, October, December
$month_digit_31		=	"(?:1|3|5|7|8|10|12)";

# April, June, September, November
$month_digit_30		=	"(?:4|6|9|11)";

# February
$month_digit_29		=	"2";

# With "0" included
$month_digit_0		=	"(?:0[1-9]|1[12])";

# 1100 - 2999, 1950s, '60s
$year_format		=	"(?:(?:1[1-9][0-9][0-9]|2[0-9][0-9][0-9])s?)";

# 00 - 99
$year_short_format	=	"(?:[0-9][0-9])";

%month_num		=	qw(01 January 02 February 03 March 04 April 05 May 06 June 07 July 08 August 09 September 10 October 11 November 12 December);

%month_abbreviation	=	qw(Jan. January Feb. February Mar. March Apr. April Jun. June Jul. July Aug. August Sep. September Oct. October Nov. November Dec. December Jan January Feb February Mar March Apr April Jun June Jul July Aug August Sep September Oct October Nov November Dec December);

$weekday		=	"(?:Monday|Tuesday|Wednesday|Thursday|Friday|Saturday|Sunday)";
#$weekday_def		=	"(?:måndagen|tisdagen|onsdagen|torsdagen|fredagen|lördagen|söndagen)";
$weekday_abbr		=	"(?:Mon\.?|Tue\.?|Wed\.?|Thu?\.?|Fri?\.?|Sat\.?|Sun?\.?)"; #|må|ti|on|to|fr|lö|sö)";

$num_ord_ending		=	"(?:\\'?(?:st|nd|th|s))";

$time_words		=	"$month_letter_format|spring|summer|autumn|winter|year|years|month|easter|christmas|born|dead|died|period|dated|until";

#***************************************************************#
#	Delimiters						#
#***************************************************************#
$maj_del	=	"\.\!\?";
$min_del	=	"\,\;\:\(\)\/";
$quote		=	"\"\'";
$other_del	=	quotemeta("\§\½\@\#\£\%\&\/\[\]\=\{\}\´\`\¨\^\~\*\<\>\|\_\-\+\\");
$all_delimiters	=	"$maj_del . $min_del . $quote";

#***************************************************************#
#	Default values for sublists				#
#***************************************************************#
$default_emphasis	=	"1";
$default_expansion	=	"0";
$default_homo_across	=	"0";
$default_homo_within	=	"0";
$default_lang		=	"eng";
$default_morphology	=	"0";
$default_pos		=	"UNK";
$default_parse		=	"0";
$default_parse_number	=	"0";
$default_phrase_depth	=	"0";
$default_dependency	=	"0";
$default_pause		=	"0";
$default_rate		=	1;
$default_transcription	=	"0";
$default_voice		=	"F1";
$default_type		=	"NORM";
