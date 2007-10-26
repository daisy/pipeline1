#!/usr/bin/perl -w

#***************************************************************#
#	ties lists of...					#
#								#
#	CE 070307						#
#***************************************************************#
use DB_File;

sub tie_lists {
	tie (%override_list,"DB_File",$override_db_file,O_RDWR|O_CREAT, 0666) or die "Cannot tie $override_db_file: $!";
	tie (%misspell,"DB_File",$misspell_db_file,O_RDWR|O_CREAT, 0666) or die "Cannot tie $misspell_db_file: $!";
	tie (%phone_list,"DB_File",$phone_db_file,O_RDWR|O_CREAT, 0666) or die "Cannot tie $phone_db_file: $!";
	tie (%lang_homo,"DB_File",$lang_homo_db_file,O_RDWR|O_CREAT, 0666) or die "Cannot tie $lang_homo_db_file: $!";
	tie (%char_norm_list,"DB_File",$char_norm_db_file,O_RDWR|O_CREAT, 0666) or die "Cannot tie $char_norm_db_file: $!";
	tie (%char_trk_list,"DB_File",$char_trk_db_file,O_RDWR|O_CREAT, 0666) or die "Cannot tie $char_trk_db_file: $!";
#	tie (%domain_list,"DB_File",$domain_db_file,O_RDWR|O_CREAT, 0666) or die "Cannot tie $domain_db_file: $!";
#	tie (%homograph_list,"DB_File",$homograph_db_file,O_RDWR|O_CREAT, 0666) or die "Cannot tie $homograph_db_file: $!";
#	tie (%homograph_type_list,"DB_File",$homograph_type_db_file,O_RDWR|O_CREAT, 0666) or die "Cannot tie $homograph_type_db_file: $!";
	tie (%initial_c_list,"DB_File",$initial_c_db_file,O_RDWR|O_CREAT, 0666) or die "Cannot tie $initial_c_db_file: $!";	
	tie (%initial_v_list,"DB_File",$initial_v_db_file,O_RDWR|O_CREAT, 0666) or die "Cannot tie $initial_v_db_file: $!";	
	tie (%medial_c_list,"DB_File",$medial_c_db_file,O_RDWR|O_CREAT, 0666) or die "Cannot tie $medial_c_db_file: $!";	
	tie (%medial_v_list,"DB_File",$medial_v_db_file,O_RDWR|O_CREAT, 0666) or die "Cannot tie $medial_v_db_file: $!";	
	tie (%final_c_list,"DB_File",$final_c_db_file,O_RDWR|O_CREAT, 0666) or die "Cannot tie $final_c_db_file: $!";	
	tie (%final_v_list,"DB_File",$final_v_db_file,O_RDWR|O_CREAT, 0666) or die "Cannot tie $final_v_db_file: $!";	
#	tie (%character_list,"DB_File",$character_db_file,O_RDWR|O_CREAT, 0666) or die "Cannot tie $character_db_file: $!";	
#	tie (%unit_list,"DB_File",$unit_db_file,O_RDWR|O_CREAT, 0666) or die "Cannot tie $unit_db_file: $!";	
	tie (%num_trk,"DB_File",$num_trk_db_file,O_RDWR|O_CREAT, 0666) or die "Cannot tie $num_trk_db_file: $!";	
	tie (%alphabet,"DB_File",$alphabet_db_file,O_RDWR|O_CREAT, 0666) or die "Cannot tie $alphabet_db_file: $!";	
	tie (%acronym_list,"DB_File",$acronym_db_file,O_RDWR|O_CREAT, 0666) or die "Cannot tie $acronym_db_file: $!";	
	#tie (%abbreviation_list,"DB_File",$abbreviation_db_file,O_RDWR|O_CREAT, 0666) or die "Cannot tie $abbreviation_db_file: $!";	
	tie (%en_lexicon,"DB_File",$en_lexicon_db_file,O_RDWR|O_CREAT, 0666) or die "Cannot tie $en_lexicon_db_file: $!";	
	tie (%en_lexicon_tags,"DB_File",$en_lexicon_tags_db_file,O_RDWR|O_CREAT, 0666) or die "Cannot tie $en_lexicon_tags_db_file: $!";	
	#tie (%no_lexicon,"DB_File",$no_lexicon_db_file,O_RDWR|O_CREAT, 0666) or die "Cannot tie $no_lexicon_db_file: $!";	
	#tie (%no_lexicon_tags,"DB_File",$no_lexicon_tags_db_file,O_RDWR|O_CREAT, 0666) or die "Cannot tie $no_lexicon_tags_db_file: $!";	
	tie (%extra_lexicon,"DB_File",$extra_lexicon_db_file,O_RDWR|O_CREAT, 0666) or die "Cannot tie $extra_lexicon_db_file: $!";	
	tie (%extra_lexicon_tags,"DB_File",$extra_lexicon_tags_db_file,O_RDWR|O_CREAT, 0666) or die "Cannot tie $extra_lexicon_tags_db_file: $!";	
	tie (%lexicon,"DB_File",$lexicon_db_file,O_RDWR|O_CREAT, 0666) or die "Cannot tie $lexicon_db_file: $!";	
	tie (%lex_trk,"DB_File",$lex_trk_db_file,O_RDWR|O_CREAT, 0666) or die "Cannot tie $lex_trk_db_file: $!";	
}
#***************************************************************#
1;
