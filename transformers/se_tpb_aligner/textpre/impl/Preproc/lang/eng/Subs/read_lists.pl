#! /usr/bin/perl -w

use DB_File;

sub read_lists {

	my $lang = shift;

	
	&read_delimiter_list("0");
	&read_num_transcr("0");
# Not for English aligning!	&read_eng_lexicon("0");

#	&read_taglex("0");
#	&read_extra_lexicon("0");
	&read_namelex("0");
	&read_acronym_list("1");
	&read_abbreviation_list("1");
	&read_unit_list("0");
	&read_alphabet("0");
	&read_domain_list("0");
	&read_tpa2cmu("0");
	&read_char_norm_list("0");
	
}
#***************************************************************#
# read_char_norm_list
#***************************************************************#
sub read_char_norm_list {
	
	my $update		=	shift;

	$char_norm_file		=	"$preproc_path/lang/$lang/Lists/char_norm.txt";
	$char_norm_db_file	=	"$preproc_path/lang/$lang/DB/char_norm.db";
	
	# Tie to hash
	tie (%char_norm,"DB_File",$char_norm_db_file,O_RDWR|O_CREAT, 0666) or die "Cannot tie $char_norm_db_file: $!";
	
	# Read list
	if ($update == 1) {
		%char_norm = ();
		
		open CHARNORM,"$char_norm_file" or die "Cannor open $char_norm_file: $!";
		while (<CHARNORM>) {
			chomp;
			next if /^\#/;
			my @c_list = split/\t+/;
			$char_norm{ $c_list[0] } = $c_list[2];
		}
		close CHARNORM;
	}

	@char_norm_list = keys %char_norm;
	$char_norm_list = join"\|",@char_norm_list;
	
}
#***************************************************************#
# read_tpa2cmu
#***************************************************************#
sub read_tpa2cmu {
	
	my $update		=	shift;

	$tpa2cmu_file		=	"$preproc_path/lang/$lang/Lists/tpa2cmu.txt";
	$tpa2cmu_db_file	=	"$preproc_path/lang/$lang/DB/tpa2cmu.db";
	
	# Tie to hash
	tie (%tpa2cmu,"DB_File",$tpa2cmu_db_file,O_RDWR|O_CREAT, 0666) or die "Cannot tie $tpa2cmu_db_file: $!";
	
	# Read list
	if ($update == 1) {
		
		%tpa2cmu = ();
	
		open TPA2CMU,"$tpa2cmu_file" or die "Cannot open $tpa2cmu_file: $!";
		while (<TPA2CMU>) {
			chomp;
			my ($tpa,$cmu,$rest)	=	split/\t+/;
			$tpa2cmu{ $tpa }	=	$cmu;
		}
		close TPA2CMU;
	}
}

#***************************************************************#
# read_num_transcr
#***************************************************************#
sub read_num_transcr {
	my $update	=	shift;

	$num_transcr_file	=	"$preproc_path/lang/$lang/Lists/num_transcr.txt";
	$num_transcr_db_file	=	"$preproc_path/lang/$lang/DB/num_transcr.db";
	
	# Tie to hash
	tie (%num_trk,"DB_File",$num_transcr_db_file,O_RDWR|O_CREAT, 0666) or die "Cannot tie $num_transcr_db_file: $!";
	
	# Read list
	if ($update == 1) {
		
		%num_trk = ();
	
		open NUMTRANSCR,"$num_transcr_file" or die "Cannot open $num_transcr_file: $!";
		while (<NUMTRANSCR>) {
			chomp;
			my ($ort,$trk)		=	split/\t+/;
			$num_trk{ $ort }	=	$trk;
		}
		close NUMTRANSCR;
	}
}
#***************************************************************#
# read_domain_list
#***************************************************************#
sub read_domain_list {
	my $update	=	shift;

	$domain_list_file	=	"$preproc_path/lang/$lang/Lists/domain_list.txt";
	$domain_list_db_file	=	"$preproc_path/lang/$lang/DB/domain_list.db";
	
	# Knyt listan till hash.
	tie (%domain_list,"DB_File",$domain_list_db_file,O_RDWR|O_CREAT, 0666) or die "Cannot tie $domain_list_db_file: $!";
	
	# Lexikon har uppdaterats och ska l�sas in p� nytt.
	if ($update == 1) {
		
		%domain_list = ();
	
		open DOMAIN,"$domain_list_file" or die "Cannot open $domain_list_file: $!";
		while (<DOMAIN>) {
			chomp;
			my ($ort,$trk) = split/\t+/;
			$domain_list{ $ort }	=	$trk;
		}
	}
	# Sortera i f�rsta hand p� f�rkortningens l�ngd f�r att matcha l�ngsta f�rst senare.		
	@domain_list = sort { length($b) <=> length($a) || $a cmp $b } keys %domain_list;
			
	# F�rkortningarnas ortografi.
	$domain_list = join"\|",@domain_list;

#	while (($k,$v) = each(%domain_list)) { print "$k\t$v\n"; }
		
}
#***************************************************************#
# read_extra_lexicon	
#***************************************************************#
sub read_extra_lexicon {

	my $update = shift;
	
	$extra_lexicon_file		=	"C:/TPB/Textprocessning/Textprocessning/Lexicon/extra_lexicon.lex";
	$extra_lexicon_db_file		=	"$preproc_path/lang/$lang/DB/extra_lexicon.db";
	$extra_lexicon_tags_db_file	=	"$preproc_path/lang/$lang/DB/extra_lexicon_tags.db";
	
	# Knyt listan till hash.
	tie (%extra_lexicon,"DB_File",$extra_lexicon_db_file,O_RDWR|O_CREAT, 0666) or die "Cannot tie $extra_lexicon_db_file: $!";	
	tie (%extra_lexicon_tags,"DB_File",$extra_lexicon_tags_db_file,O_RDWR|O_CREAT, 0666) or die "Cannot tie $extra_lexicon_tags_db_file: $!";	
	
	# Lexikon har uppdaterats och ska l�sas in p� nytt.
	if ($update == 1) {
		
		%extra_lexicon = ();
		
		open EXTRALEX,"$extra_lexicon_file" or die "Cannot open $lexicon_path/$file: $!";
		while (<EXTRALEX>) {
			chomp;
			my ($ort,$trk,$rest) = split/ \# /;

			# Remove redundant length marks.
			$trk	=~	s/($consonants_trk)[:\.]/$1/g;
			$trk	=~	s/($vowels_trk)\./$1/g;

			$ort = lc($ort);
			
			# Language
			my $lang = "eng";
			if ( $rest =~ /(lang:[^ ]+)( |$)/ ) {
				$lang = $1;
			}
		
			$rest =~ s/^ *PM[ _](...).*$/PM $1/;

	
			if (not(exists($extra_lexicon{$ort}))) {
				
			
				# 070125 Alla ord med engelsk spr�ktagg finns nu i det engelska lexikonet med f�rsvenskade uttal.
				# De ska inte l�sas in h�r.
				if ($rest !~ /lang:(?:en)/) {
					
					$rest =~ s/ (?:lang|new|orig|freq):.*$//;
					$extra_lexicon{$ort} = $trk;
					$extra_lexicon_tags{$ort} = "$rest\t$lang";
				}
			}
		}
		close EXTRALEX;
	} # end if $update
}
#***************************************************************#
# read_acronym_list	
#***************************************************************#
sub read_acronym_list {
	my $update = shift;

	#$acronym_list_file = "C:/TPB/Preproc/lang/$lang/Lists/acronym_list.txt";
	$acronym_list_file = "$preproc_path/lang/eng/Lists/acronym_list.txt";	
	$acronym_db_file = "$preproc_path/lang/eng/DB/acronym.db";


	# Knyt listan till hash.
	tie (%acronym_list,"DB_File",$acronym_db_file,O_RDWR|O_CREAT, 0666) or die "Cannot tie $acronym_db_file: $!";	
	
	# Listan har uppdaterats och ska l�sas in p� nytt.
	if ($update == 1) {

		%acronym_list = ();

		open ACRRLIST,"$acronym_list_file" or die "Cannot open $acronym_list_file: $!";
		while (<ACRRLIST>) {
			chomp;

			next if /^\#/;
			
			($orthography,$trk,$rest) = split/ \# /;

			# Remove redundant length marks.
			$trk	=~	s/($consonants_trk)[:\.]/$1/g;
			$trk	=~	s/($vowels_trk)\./$1/g;

			$acronym_list{$orthography} = "$trk" . "\t" . "$rest";
			
			
		} # end while <ACRRLIST>
		close ACRRLIST;

	} # end if $update == 1

	# Sortera i f�rsta hand p� f�rkortningens l�ngd f�r att matcha l�ngsta f�rst senare.		
	@acronym_list = sort { length($b) <=> length($a) || $a cmp $b } keys %acronym_list;

			
	# F�rkortningarnas ortografi.
	$acronym_list = join"\|",@acronym_list;

}
#***************************************************************#
# read_delimiter_list
#***************************************************************#
sub read_delimiter_list {
	
	
	open LIST,"$preproc_path/lang/$lang/Lists/delimiter_list.txt";
	while (<LIST>) {
		chomp;
		
		my ($delimiter,$expansion) = split/\t+/;
		$delimiter = quotemeta($delimiter);
		$delimiter{$delimiter} = $expansion;
		
	}
	close LIST;
}
#***************************************************************#
sub read_unit_list {
	my $update = shift;

	$unit_list_file = "C:/TPB/Preproc/lang/$lang/Lists/unit_list.txt";
	$unit_db_file = "$preproc_path/lang/$lang/DB/unit_list.db";

	# Knyt listan till hash.
	tie (%unit_list,"DB_File",$unit_db_file,O_RDWR|O_CREAT, 0666) or die "Cannot tie $unit_db_file: $!";	
	
	
	# M�ste quotemetas p.g.a "$", annars blir det en <UNIT>-tagg sist p� varje rad.
	# Listan har uppdaterats och ska l�sas in p� nytt.
	if ($update == 1) {

		%unit_list = ();

		open UNITLIST,"$unit_list_file" or die "Cannot open $unit_list_file: $!";
		while (<UNITLIST>) {
			chomp;
			
			my $line = $_;

			next if /^\#/;
			
			# Om expansion finns, l�gg den som v�rde.
			if ($line =~ /\t/) {
			
				($unit,$exp) = split/\t+/,$line;
				$unit = quotemeta($unit);
				$unit_list{$unit} = $exp;

			} else {
				$line = quotemeta($line);
				$unit_list{$unit} = "-";
		
			}
				
		} # end while <UNITLIST>
		close UNITLIST;
	
	}
		
#	$x = "mhz";
#	if (exists ($unit_list{$x})) { print "JJJJAAA $x\t$unit_list{$x}\n\n"; }

	# Sortera i f�rsta hand p� enhetens l�ngd f�r att matcha l�ngsta f�rst senare.		
	@unit_list = sort { length($b) <=> length($a) || $a cmp $b } keys %unit_list;
			

	$unit_list = join"\|",@unit_list;
	
	
}
#***************************************************************#
sub read_taglex {

	my $update = shift;
		
		
	$lexicon_file		=	"C:/TPB/Textprocessning/Lex/Tagg/suo-train-id_tags15_ort.lex";
	$new_lexicon_db_file	=	"$preproc_path/lang/$lang/DB/new_lexicon.db.";
	$new_lex_trk_db_file	=	"$preproc_path/lang/$lang/DB/new_lex_trk_db_file";
			
	# Knyt listan till hash.
	tie (%new_lexicon,"DB_File",$new_lexicon_db_file,O_RDWR|O_CREAT, 0666) or die "Cannot tie $new_lexicon_db_file: $!";	
	tie (%new_lex_trk,"DB_File",$new_lex_trk_db_file,O_RDWR|O_CREAT, 0666) or die "Cannot tie $new_lex_trk_db_file: $!";	
	
#	$lexicon_file		=	"C:/TPB/Textprocessning/Lex/Tagg/testlex.lex";
#	$new_lexicon_db_file2	=	"$preproc_path/lang/$lang/DB/new_lexicon2.db.";
#	$new_lex_trk_db_file2	=	"$preproc_path/lang/$lang/DB/new_lex_trk_db_file2";

#	tie (%new_lexicon,"DB_File",$new_lexicon_db_file2,O_RDWR|O_CREAT, 0666) or die "Cannot tie $new_lexicon_db_file2: $!";	
#	tie (%new_lex_trk,"DB_File",$new_lex_trk_db_file2,O_RDWR|O_CREAT, 0666) or die "Cannot tie $new_lex_trk_db_file2: $!";	
	
	
	# Lexikon har uppdaterats och ska l�sas in p� nytt.
	if ($update == 1) {

		%new_lexicon = ();
		%new_lex_trk = ();
		
		# Skapa hash med ortografi som nyckel och pos-taggar som v�rde.
		open LEXICON,"$lexicon_file" or die "Cannot open $lexicon_file: $!";
		while (<LEXICON>) {
			chomp;
#			next if $_ !~ /^and\t/;
			&create_lexicon_hash($_);
		
#					print "\n-------\nNU: $_\n";
			
			#while (($k,$v) = each(%new_lex_trk)) { print "1xx $k\t$v\n"; }
				
		} # end while <LEXICON>
		close LEXICON;
		
	} # end if $update == 1
}
#################################################################################
# create_lexicon_hash		Skapar hash med ortografi och taggar		#
#################################################################################
sub create_lexicon_hash {
	
	my $line = shift;
	my ($ort,$trk,$tags) = split/\t/,$line;

	# Remove redundant length marks.
	$trk	=~	s/($consonants_trk)[:\.]/$1/g;
	$trk	=~	s/($vowels_trk)\./$1/g;

	my $lang = "swe";

#	print "TT $tags\n\n";

	# Catch $lang.
	if ($line =~ /\tlang:([^\t][^\t]+|-)\t/) {
		$lang = $1;
		$lang =~ s/-/swe/;
	} 

#	print "\n------------\n$line\nLAGN: $lang\n\n";
	
	# 070125 Alla ord med engelsk spr�ktagg finns nu i det engelska lexikonet med f�rsvenskade uttal.
	# De ska inte l�sas in h�r.
	if ($lang !~ /^(?:en|no)$/) {

#		print "LANG: $lang\n\n";
		
		# Plocka fram pos-taggarna.
		$tags =~ s/\s+(lang|<id|gendecomp).*//;
		
#		print "TTT $tags\n";

		my $checker = 1;

		# Orthography already exists in hash list.
		if (exists($new_lexicon{$ort})) {
			
			my $checker = 0;

			# Check if exists with same tags and transciption.
			if (@new_lexicon = split/\|/,$new_lexicon{$ort}) {
				my $tcounter	=	0;
				#my $checker	=	0;
				
				# Split transciptions into list.
				my @transcriptions = split/\|/,$new_lex_trk{$ort};

					
				
				# Check if exists with identival tags and transcription.
				foreach my $nl (@new_lexicon) {

					my $test_nl	=	$nl;
					$test_nl	=~	s/\t.+//;
					#print "NL $nl\n";
					# print "\n\nCH: $nl\t$tags\n$transcriptions[$tcounter]\t$trk\n\n";

					if ($test_nl eq $tags && $transcriptions[$tcounter] eq $trk) {
						#print "\n\n---------------\nLIKALIKA\nCHECKER: $checker\n$nl\t$tags\n$transcriptions[$tcounter]\t$trk\n\n";
						$checker = 0;
					} else {
						#print "\n\n---------------\nOLIKAOLIKA\nCHECKER: $checker\nTAGS: $nl ___\t$tags\nTRANS: $transcriptions[$tcounter] ___\t$trk\n\n";
						$checker = 1;
					}

#					print "\n\n---------------\nCHECKER: $checker\n1. $test_nl\t$tags\n2. $transcriptions[$tcounter]\t$trk\n\n";

					$tcounter++;
	
				}
			}
			
#			print "TT2 $trk\t$transcriptione{$pcounter}\n"; sleep 1;
			
			if ($checker == 1) {
				
				#print "TJOTJO $new_lexicon{$ort}\t$new_lex_trk{$ort}\nTL $tags\n\n";
			
				# PoS is not unknown
				if ($tags ne "XXX") {
					
				#	print "$ort\tTAGS: $tags\tLANG: $lang\n\n";
					
					$new_lexicon{$ort}	.=	"|$tags\t$lang";
					$new_lex_trk{$ort}	.=	"|$trk";
				#	print "UUUd $new_lex_trk{$ort}\nUUUe $new_lexicon{$ort}\n\n";
					
				}
			}
	#		#print "\n\n\nooooooooooooooooooooooooo\nCHECKER: $checker\n$new_lex_trk{$ort}\n\n";
			
			
		# Har inte f�rekommit tidigare.
		} else {
			
#			print "TT1 $trk\t$transcription{$ort}\n";
			
			$new_lexicon{$ort} = "$tags\t$lang";
			$new_lex_trk{$ort} = $trk;
			
#			print "UUU $new_lex_trk{$ort}\nVVV $new_lexicon{$ort}\n";
			
		}
	
	}


}
#################################################################################
# read_abbreviation_list	L�ser f�rkortningslista/databas			#
#################################################################################
sub read_abbreviation_list {
	my $update = shift;

	$abbreviation_list_file	=	"$preproc_path/lang/$lang/Lists/abbreviation_list.txt";
	$abbreviation_db_file	=	"$preproc_path/lang/$lang/DB/abbreviation_list.db.";

	# Knyt listan till hash.
	tie (%abbreviation_list,"DB_File",$abbreviation_db_file,O_RDWR|O_CREAT, 0666) or die "Cannot tie $abbreviation_db_file: $!";	
	
	# Listan har uppdaterats och ska l�sas in p� nytt.
	if ($update == 1) {

		%abbreviation_list = ();

		open ABBRLIST,"$abbreviation_list_file" or die "Cannot open $abbreviation_list_file: $!";
		while (<ABBRLIST>) {
			chomp;
			
			next if /^\#/;

			#$line = lc($_);
			#$line = lowercase($_);
			$line = $_;
						
			my ($orthography,$expansions,$context,$mayend,$casesensitive) = split/ *\t+ */,$line;
			
			
			if ($casesensitive == 0){
				#$orthography = &lowercase($orthography);
				$orthography = lc($orthography);
			}
			
			$orthography = quotemeta($orthography);
			$expansions = quotemeta($expansions);
			#$context = quotemeta($context);

			$abbreviation_list{$orthography} = "exp=" . $expansions . "\tcon=" . $context . "\tcas=" . $casesensitive;
			
			
			
		} # end while <ABBRLIST>
		close ABBRLIST;

	} # end if $update == 1

	
	# Sortera i f�rsta hand p� f�rkortningens l�ngd f�r att matcha l�ngsta f�rst senare.		
	@abbreviation_list = sort { length($b) <=> length($a) || $a cmp $b } keys %abbreviation_list;
			
	# F�rkortningarnas ortografi.
	$abbreviation_list = join"\|",@abbreviation_list;


}
#################################################################################
sub read_namelex {

	my $update = shift;
		
	$namelex_file		=	"C:/TPB/Textprocessning/Lex/tpblex_namn.lex";
	$name_db_file		=	"$preproc_path/lang/$lang/DB/name_lexicon.db.";
	$name_tags_db_file	=	"$preproc_path/lang/$lang/DB/name_tags_lexicon.db.";

	# Knyt listan till hash.
	tie (%name_lexicon,"DB_File",$name_db_file,O_RDWR|O_CREAT, 0666) or die "Cannot tie $name_db_file: $!";	
	tie (%name_lexicon_tags,"DB_File",$name_tags_db_file,O_RDWR|O_CREAT, 0666) or die "Cannot tie $name_tags_db_file: $!";	
		
	
	# Lexikon har uppdaterats och ska l�sas in p� nytt.
	if ($update == 1) {

		%name_lexicon = ();
		
		# Skapa hash med ortografi som nyckel och pos-taggar som v�rde.
		open LEXICON,"$namelex_file" or die "Cannot open $namelex_file: $!";
		while (<LEXICON>) {
			chomp;

			
			my ($name,$trk,$rest) = split/ \# /;

			$rest =~ s/^ *PM[ _](...).*$/PM $1/;

	
			$name_lexicon{ $name } 		=	$trk;
			$name_lexicon_tags{ $name}	=	$rest;
			
				
		} # end while <LEXICON>
		close LEXICON;
		
	} # end if $update == 1

}
#################################################################################
# read_alphabet		L�ser alfabetsfil med ortografi och transkription	#
#################################################################################
sub read_alphabet {
	
	
	my $update = shift;

	$alphabet_file		=	"$preproc_path/lang/$lang/Lists/alphabet.txt";
	$alphabet_db_file	=	"$preproc_path/lang/$lang/DB/alphabet.db";
	
	
	# Knyt listan till hash.
	tie (%alphabet,"DB_File",$alphabet_db_file,O_RDWR|O_CREAT, 0666) or die "Cannot tie $alphabet_db_file: $!";	
	
	if ($update == 1) {
	
		%alphabet = ();
		
		open ALPHABET,"$alphabet_file" or die "Cannot open $alphabet_file: $!";
		while (<ALPHABET>) {
			chomp;
			
			next if $_ !~ /[a-z���]/i;
			
			my ($ort,$trk,$tag) = split/ \# /;

			
			# Om man inte vill ha med konsonantl�ngd.
#			if ($consonant_length eq "false") {
			$trk =~ s/($consonants_trk)\:/$1/og;
#			}
			
			$alphabet{$ort} = $trk;
		}
		close ALPHABET;

	} # end if $update == 1
}
#################################################################################
# read_eng_lexicon		L�ser 5K engelskt lexikon och transkriptioner	#
#################################################################################
sub read_eng_lexicon {
	my $update = shift;
	
	
	my $eng_lexicon_file		=	"C:/TPB/Textprocessning/Textprocessning/Lexicon/eng4.lex";
	
	my $eng_lexicon_tags_db_file	=	"$preproc_path/lang/$lang/DB/eng_lexicon_tags.db";
	my $eng_lexicon_db_file		=	"$preproc_path/lang/$lang/DB/eng_lexicon.db";
	
	
	# Knyt listan till hash.
	tie (%eng_lexicon,"DB_File",$eng_lexicon_db_file,O_RDWR|O_CREAT, 0666) or die "Cannot tie $eng_lexicon_db_file: $!";	
	tie (%eng_lexicon_tags,"DB_File",$eng_lexicon_tags_db_file,O_RDWR|O_CREAT, 0666) or die "Cannot tie $eng_lexicon_tags_db_file: $!";	
	
	# Lexikon har uppdaterats och ska l�sas in p� nytt.
	if ($update == 1) {
		
		%eng_lexicon = ();
		
		open ENGLEX,"$eng_lexicon_file" or die "Cannot open $eng_lexicon_file: $!";
		while (<ENGLEX>) {
			chomp;
			
			next if /^\#/;
			my ($ort,$trk,$rest) = split/ \# /;

			# Remove redundant length marks.
			$trk	=~	s/($consonants_trk)[:\.]/$1/g;
			$trk	=~	s/($vowels_trk)\./$1/g;

			#$ort = lc($ort);
				
			my ($pos,$lang,$pronvar,$spell,$freq,$update,$orig,$lemma,$id) = split/ \# /,$rest;
			
			$pos =~ s/^([A-Z][A-Z ]+)(?:lang|new|freq|update).*$/$1/;

			if ($ort ne "I") {
				$ort = lc($ort);
			}
			
			
			if (not(exists($eng_lexicon{$ort}))) {
				$eng_lexicon{$ort} = $trk;
				$eng_lexicon_tags{$ort} = $pos;
			}
		}
		close ENGLEX;
	} # end if $update
} # end sub
#***********************************************************************************#
1;
