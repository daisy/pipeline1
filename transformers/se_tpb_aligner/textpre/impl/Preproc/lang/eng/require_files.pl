#!/usr/bin/perl -w

#$preproc_path = "C:/TPB/Preproc";
$lang = "eng";

require "$preproc_path/lang/$lang/Subs/process_string.pl";
require "$preproc_path/lang/$lang/Subs/sentence_split.pl";
require "$preproc_path/lang/$lang/Subs/count_components.pl";
require "$preproc_path/lang/$lang/Subs/create_sublists.pl";
require "$preproc_path/lang/$lang/Subs/print_all_output.pl";
#require "$preproc_path/lang/$lang/Subs/tie_lists.pl";
require "$preproc_path/lang/$lang/Subs/misc_subs.pl";
require "$preproc_path/lang/$lang/Subs/read_lists.pl";
require "$preproc_path/lang/$lang/Subs/lookup_subs.pl";
require "$preproc_path/lang/$lang/Subs/tpa2cmu.pl";
require "$preproc_path/lang/$lang/Subs/character_encoding.pl";

require "$preproc_path/lang/$lang/Subs/run_rules.pl";

#require "$preproc_path/lang/$lang/Rules/stress_rules.pl";

#require "$preproc_path/lang/$lang/Rules/unit_rules.pl";
require "$preproc_path/lang/$lang/Rules/initials_rules.pl";
require "$preproc_path/lang/$lang/Rules/date_rules.pl";
require "$preproc_path/lang/$lang/Rules/time_rules.pl";
require "$preproc_path/lang/$lang/Rules/email_rules.pl";
require "$preproc_path/lang/$lang/Rules/url_rules.pl";
require "$preproc_path/lang/$lang/Rules/filename_rules.pl";
require "$preproc_path/lang/$lang/Rules/acronym_rules.pl";

require "$preproc_path/lang/$lang/Rules/merge_num_rules.pl";
require "$preproc_path/lang/$lang/Rules/decimal_num_rules.pl";
require "$preproc_path/lang/$lang/Rules/ordinals_num_rules.pl";
#require "$preproc_path/lang/$lang/Rules/interval_num_rules.pl";

require "$preproc_path/lang/$lang/Rules/currency_rules.pl";

#require "$preproc_path/lang/$lang/Rules/phone_num_rules.pl";
require "$preproc_path/lang/$lang/Rules/mixed_num_rules.pl";
require "$preproc_path/lang/$lang/Rules/roman_num_rules.pl";
require "$preproc_path/lang/$lang/Rules/year_num_rules.pl";

require "$preproc_path/lang/$lang/Rules/expansion_rules.pl";
require "$preproc_path/lang/$lang/Rules/numeral_expansion_rules.pl";
require "$preproc_path/lang/$lang/Rules/character_expansion_rules.pl";
require "$preproc_path/lang/$lang/Rules/abbreviation_expansion_rules.pl";
require "$preproc_path/lang/$lang/Rules/acronym_expansion_rules.pl";
require "$preproc_path/lang/$lang/Rules/unit_expansion_rules.pl";
require "$preproc_path/lang/$lang/Rules/currency_expansion_rules.pl";

#require "$preproc_path/lang/$lang/Rules/language_detection.pl";
#require "$preproc_path/lang/$lang/Rules/homograph_disambiguation.pl";

#require "C:/TPB/Textprocessning/Textprocessning/Scripts/Variables/paths.txt";
require "$preproc_path/lang/$lang/Vars/vars.pl";
require "$preproc_path/lang/$lang/Subs/numbers.pl";

require "$preproc_path/SSML/ssml_output.pl";
