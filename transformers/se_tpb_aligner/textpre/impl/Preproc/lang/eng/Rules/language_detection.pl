#!/usr/bin/perl -w

#***************************************************************#
# language_detection
#***************************************************************#
sub language_detection {


	my $lang = shift;

	@word_list = sort ( keys ( %orthography ));
	
	# 1. All tokens in string are possibly English, at least one is English only.
	&lang_det_1($lang);
		
	# If first rule applied, don't run the rest.
		
	# 2a. Preceding word is English only, following is possibly English.
	# 2b. Preceding word is possibly English only, following is English only.
	&lang_det_2($lang);
	
	# 3a. Is first word of string and next word is English.
	# 3b. Is last word of string and preceding word is English.
	&lang_det_3($lang);

	# 4a. Is first word after delimiter and next word is English.
	# 4b. Is last word before delimiter and next word is English.
	&lang_det_4($lang);
	
}

#***************************************************************#
# lang_det_1
# All tokens in string are possibly English, at least one is English only.
#
#***************************************************************#
sub lang_det_1 {

	my ($lang) = @_;
	
	# One of the words must be only $lang.
	my $safety_check = 0;
	
	# All of the other words must be possibly $lang
	my $lang_check = 1;
	
	
	# Go through entire word list.
	foreach my $loc ( @word_list ) {
	
#		print "PWD: $loc\tO. $orthography{$loc}\tP. $pos{ $loc }\tL. $lang{ $loc }\n\n";
	
		# PoS is not NUM or DEL and contains at least two letters.
		if ( $pos{ $loc } !~ /(?:NUM|DEL)/) {					# Not necessary? && $orthography{ $loc } =~ /[a-z][a-z]/i) {
	
#			print "OK: $loc\t$orthography{$loc}\t$pos{ $loc }\t$lang{ $loc }\n\n";
	
			# Word is only $lang
			if ( $lang{ $loc }	eq	"$lang" ) {
				$safety_check = 1;
				
			} elsif ( $lang{ $loc }	!~	/(?:$lang\||\|$lang|^$default_lang$)/ ) {
				$lang_check = 0;
			}
		}
	}
	
	
	# If conditions fulfilled
	if ( $safety_check == 1 && $lang_check == 1 ) {
		
		foreach my $loc ( @word_list ) {
	
			# Find $lang word's location in list.		
			my @lang_list	=	split/\|/,$lang{ $loc };


			my $lnumber	=	0;
			my $lcounter	=	0;

			#if ( $#lang_list != 0) {
								
				foreach my $ll (@lang_list) {
		
					if ( $ll =~ /$lang/ ) {
						$lnumber = $lcounter;
					}
					$lcounter++;
				} # end foreach @lang_list
		
			# Find correct location of value in other lists.
			&lang_change_fields($loc,$lnumber,$lang);
	
		} # end foreach @word_list
	} # end if $safety_check
}
#***************************************************************#
# lang_det_2
# Preceding word is English only, following is possibly English.
# Preceding word is possibly English only, following is English only.
#
#***************************************************************#
sub lang_det_2 {

	my $lang = shift;

	my $counter = 0;

	# Go through entire word list.
	foreach my $loc ( @word_list ) {

		if ( $counter > 1 && $counter < $#word_list -1 ) {
			
			my $prev = $word_list[$counter-2];
			my $foll = $word_list[$counter+2];
		
#			print "LL $orthography{$loc}\t$lang{$prev}\t$lang{$foll}\n";
		
			if ( 
				(
					$lang{ $loc }	=~	/(?:$lang\||\|$lang|^$default_lang$)/
					&&
					$pos{ $loc }	!~	/DEL/
					&&
					$lang{ $prev }	eq	"$lang"
					&&
					$lang{ $foll }	=~	/(?:$lang|^$default_lang$)/
				) || (
					$lang{ $loc }	=~	/(?:\|$lang|$lang\||^$default_lang$)/
					&&
					$pos{ $loc }	!~	/DEL/
					&&
					$lang{ $prev }	=~	/(?:$lang|^$default_lang$)/
					&&
					$lang{ $foll }	eq	"$lang"
				)
			) {
				
				
				
				
				# Find $lang word's location in list.		
				my @lang_list	=	split/\|/,$lang{ $loc };
				
#				print "RULE 2: $orthography{$loc}\t@lang_list\n\n";

				my $lnumber	=	0;
				my $lcounter	=	0;
	
				foreach my $ll (@lang_list) {
			
					if ( $ll =~ /$lang/ ) {
						$lnumber = $lcounter;
					}
						$lcounter++;
				} # end foreach @lang_list
			
				# Find correct location of value in other lists.
				&lang_change_fields($loc,$lnumber,$lang);		
				
				if ( $lang{ $prev }	ne	"$lang" ) {
					&lang_change_fields($prev,$lnumber,$lang);
				}
				if ( $lang{ $foll }	ne	"$lang" ) {
					&lang_change_fields($foll,$lnumber,$lang);
				}
					
			}	
			
			
		} # end if $counter
	
		$counter++;
	} # end foreach @word_list
}
#***************************************************************#
# lang_det_3
# Is first word of string and next word is English.
# Is last word of string and preceding word is English.
#
#***************************************************************#
sub lang_det_3 {
	
	my $lang = shift;

	if ( $#word_list > 1 ) {

		my $first = $word_list[0];
		my $foll = $word_list[2];
		my $last = $word_list[-1];
		my $prev = $word_list[-3];
		
		if ( 
			$lang{ $first }	=~	/(?:$lang\||\|$lang|^$default_lang$)/
			&&
			$pos{ $first }	!~	/DEL/
			&&
			$lang{ $foll }	=~	/$lang/

		) {
				
	
			# Find $lang word's location in list.		
			my @lang_list	=	split/\|/,$lang{ $first };

#			print "RULE 3a: $orthography{$first}\t@lang_list\n\n";

			my $lnumber	=	0;
			my $lcounter	=	0;
	
			foreach my $ll (@lang_list) {
		
				if ( $ll =~ /$lang/ ) {
					$lnumber = $lcounter;
				}
					$lcounter++;
			} # end foreach @lang_list
			
			# Find correct location of value in other lists.
			&lang_change_fields($first,$lnumber,$lang);		
					
		}	
			
			
		if ( 
			$lang{ $last }	=~	/(?:$lang\||\|$lang|^$default_lang$)/
			&&
			$pos{ $last }	!~	/DEL/
			&&
			$lang{ $prev }	=~	/$lang/

		) {
			# Find $lang word's location in list.		
			my @lang_list	=	split/\|/,$lang{ $last };
	
#			print "RULE 3b: $orthography{$last}\t@lang_list\n\n";

			my $lnumber	=	0;
			my $lcounter	=	0;
	
			foreach my $ll (@lang_list) {
		
				if ( $ll =~ /$lang/ ) {
					$lnumber = $lcounter;
				}
					$lcounter++;
			} # end foreach @lang_list
			
			# Find correct location of value in other lists.
			&lang_change_fields($last,$lnumber,$lang);		
					
		}		
		$counter++;
	} # end foreach @word_list


}
#***************************************************************#
# lang_det_4
# Is first word after delimiter and next word is English.
# Is last word before delimiter and next word is English.
#
#***************************************************************#
sub lang_det_4 {
	
	my $lang = shift;

	my $counter = 0;
	
	my $list_length = $#word_list;
	
	# First word after delimiter.
	foreach my $loc (@word_list) {

#	print "\njjjjjjjjjjjjjjjjj\n$orthography{$loc}
#	COUNTER:	$counter	<	$list_length -1 och > 1
#	POS +1:		$pos{ $word_list[$counter+1] }
#	LANG:		$lang{ $word_list[$counter-2] }
#	\n";

		# Current word is possibly English and fields before and after exist.
		if (
			$lang{ $loc }	=~	/(?:$lang\||\|$lang|^$default_lang$)/
			&&
			$pos{ $loc }	!~	/DEL/
			&&			
			(	
				$counter	<	$list_length-2
				&&
				$lang{ $word_list[$counter+2] }	=~	/$lang/
				&&
				$counter 	>	1
				&&
				$pos{ $word_list[$counter-1] }	eq	"DEL"
				&&
				$pos{ $word_list[$counter-2] }	=~	/.DEL/
		
			) || (
				$counter	<	$list_length-2
				&&
				$lang{ $word_list[$counter+2] }	=~	/$lang/
				&&
				$counter 	>	0
				&&
				$pos{ $word_list[$counter-1] }	=~	/.DEL/
				
			) || (
				$counter	<=	$list_length-1
				&&
				$pos{ $word_list[$counter+1] }	=~	/.DEL/
				&&
				$counter 	>	1
				&&
				$lang{ $word_list[$counter-2] }	=~	/$lang/
			
			)					
		) {


				
#			print "PP $pos{$prev}\tFF $lang{$foll}\n\n";
			
			# Find $lang word's location in list.		
			my @lang_list	=	split/\|/,$lang{ $loc };

#			print "RULE 4: $orthography{$loc}\t@lang_list\n\n";
#			print "LL @lang_list\n";
		
			my $lnumber	=	0;
			my $lcounter	=	0;

			foreach my $ll (@lang_list) {
		
				if ( $ll =~ /$lang/ ) {
					$lnumber = $lcounter;
				}
					$lcounter++;
			} # end foreach @lang_list
			
			# Find correct location of value in other lists.
			&lang_change_fields($loc,$lnumber,$lang);		
								
		} # end if $lang
		$counter++;
	} # end foreach $loc
}
#***************************************************************#
# lang_change_fields
#
#
#***************************************************************#
sub lang_change_fields {
	
	my ($loc,$lnumber,$lang) = @_;
	
#	print "L $loc\t$orthography{$loc}\n\n";

#	print "\n---------------------\nNU: $orthography{$loc}\t$lang{$loc}\nPPLL $pos{$loc}\n\n";

	# More information exists.
	if ( exists ( $pos{ $loc } ) && $pos{ $loc } ne "UNK") {
	
		my @pos_list		=	split/\|/,$pos{ $loc };
		my @morph_list		=	split/\|/,$morphology{ $loc };
		my @transcr_list	=	split/\|/,$transcription{ $loc };
	
#		print "JAG FINNS: $pos{ $loc}\n\n";
		
	#	print "\nxxxxxx\nLOC: $loc\tLN: $lnumber\tORT: $orthography{$loc}\nxxxxxxxxxxx\n";
	#	print "PL @pos_list\t\t$lnumber\n";
	#	print "TL @transcr_list\t\t$lnumber\n";
		
		$lang{ $loc }		=	$lang;
		
		$pos{ $loc }		=	$pos_list[$lnumber];
		$morphology{ $loc }	=	$morph_list[$lnumber];
		$transcription{ $loc }	=	$transcr_list[$lnumber];
	
	# No more information exists.	
	} else {
		$lang{ $loc }		=	$lang;
		
		$pos{ $loc }		=	"UNK";
		$morphology{ $loc }	=	"-";
		$transcription{ $loc }	=	"$default_transcription";
		
#		print "HUUU\n";
	}		
	
#	print "O. $orthography{$loc}\nL. $lang{$loc}\nP. $pos{$loc}\nM. $morphology{$loc}\nT. $transcription{$loc}\n\n";
	
}
#***************************************************************#
1;
