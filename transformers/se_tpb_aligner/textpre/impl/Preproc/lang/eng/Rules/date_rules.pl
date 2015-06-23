 #!/usr/bin/perl -w

#**************************************************************#
sub date_subs {
	
	my $index_list = shift;
	($prev_10,$prev_9,$prev_8,$prev_7,$prev_6,$prev_5,$prev_4,$prev_3,$prev_2,$prev_1,$curr,$next_1,$next_2,$next_3,$next_4,$next_5,$next_6,$next_7,$next_8,$next_9,$next_10) = split/\|/,$index_list;

	&date_interval_1();
#	&date_interval_2();
	
	&date_1();
	&date_2();
#	&date_3();
	&date_4();
	&date_5();
	&date_6();
	
#	&print_all_output();
}
#**************************************************************#
sub include_subs {

	my $index_list = shift;
	($prev_10,$prev_9,$prev_8,$prev_7,$prev_6,$prev_5,$prev_4,$prev_3,$prev_2,$prev_1,$curr,$next_1,$next_2,$next_3,$next_4,$next_5,$next_6,$next_7,$next_8,$next_9,$next_10) = split/\|/,$index_list;

	&include_year();
	&include_interval();
	&include_the();
	&include_dates();
	&rule_date_weekday();
	&include_weekday();

}
#**************************************************************#
# date_1
# Example:	1 January, 1st Jan., 1st of Jan.
#		date_1
#		16th of November 2003 
#		16th November 2003 
#		16 November 2003 
#		16 Nov 2003 
#		NUM		-->	NUM ORD
#**************************************************************#
sub date_1 {

	if (exists ($ort{ $next_2 } )) {

#		print "\n--------------\ndate_1\nCurr: $ort{$curr}\tNext_1: $pos{$next_1}\tNext_2: $ort{$next_2}\n\n";

		if (
			# Context
			(
			$type{ $curr }	ne	"DATE"
			&&
			$ort{ $curr }	=~	/^$date_digit_format$num_ord_ending?$/io	# 31
			&&
			$pos{ $next_1 }	eq	"DEL"
			&&
			$ort{ $next_2 }	=~	/^$month_letter_format$/io		# januari
			
			)
			
		) {
			
#			print "Rule: date_1 applied\n";
			# Retag
			$type{ $curr }		=	"DATE";
			$type{ $next_1 }	=	"DATE";
			$type{ $next_2 }	=	"DATE";
	
			$pos{ $curr }		=	"NUM ORD";
			$ort{ $next_2 }		=	"of " . $ort{ $next_2 };
			$transcription{ $next_2 }	=	"'å v | " . $transcription{ $next_2 };

			# Expand month abbreviations			
			if ( exists ( $month_abbreviation{ $ort{ $curr } } ) ) {
				$exp{ $curr }		=	$month_abbreviation{ $ort{ $curr } };
			}
		}
	} # end exists		
	
	# 1 of May
	if (exists ($ort{ $next_4 } )) {

#		print "\n--------------\ndate_1\nCurr: $ort{$curr}\tNext_1: $pos{$next_1}\tNext_2: $ort{$next_2}\n\n";

		if (
			# Context
			(
			$type{ $curr }	ne	"DATE"
			&&
			$ort{ $curr }	=~	/^$date_digit_format$num_ord_ending?$/io	# 31
			&&
			$pos{ $next_1 }	eq	"DEL"
			&&
			$ort{ $next_2 }	eq	"of"					# of
			&&
			$pos{ $next_3 }	eq	"DEL"
			&&
			$ort{ $next_4 }	=~	/^$month_letter_format$/io		# januari
			)
			
		) {
			
#			print "Rule: date_1 applied\n";
			# Retag
			$type{ $curr }		=	"DATE";
			$type{ $next_1 }	=	"DATE";
			$type{ $next_2 }	=	"DATE";
			$type{ $next_3 }	=	"DATE";
			$type{ $next_4 }	=	"DATE";

			# Expand month abbreviations			
			if ( exists ( $month_abbreviation{ $ort{ $curr } } ) ) {
				$exp{ $curr }		=	$month_abbreviation{ $ort{ $curr } };
			}
	
			$pos{ $curr }		=	"NUM ORD";
		}
	} # end exists		
}
#**************************************************************#
# date_2
# Example:	16/11/2003, 16.11.2003, 16-11-2003 or 16-11-03 
#		--> sixteenth of November two thousand three
#		
#**************************************************************#
sub date_2 {

#	print "
#	------------
#	date_2
#	Curr: $ort{$curr}
#	Next_1: $ort{$next_1}
#	Next_2: $ort{$next_2}
#	Next_3: $ort{$next_3}
#	Next_4: $ort{$next_4}
#	------------\n";

	if (exists ($ort{ $next_4 } )) {

		if (
			# Context
			(
			$type{ $curr }	ne	"DATE"
			&&
			$ort{ $curr }	=~	/^$date_digit_31$/	# 31
			&&
			$ort{ $next_1 }	=~	/([\/\.\-])/		# /
			&&
			$ort{ $next_2 }	=~	/^$month_digit_0$/	# 7

			&&
			$ort{ $next_3 }	eq	$ort{ $next_1 }		# /
			&&
			$ort{ $next_4 }	=~	/^$year_format$/	# 1971
			
	
		) ) {

			# Retag
			$type{ $curr }		=	"DATE";
			$type{ $next_1 }	=	"DATE";
			$type{ $next_2 }	=	"DATE";
			$type{ $next_3 }	=	"DATE";
			$type{ $next_4 }	=	"DATE";
			
			$pos{ $curr }		=	"NUM ORD";
			$exp{ $next_1 }		=	"of";
			$exp{ $next_2 }		=	$month_num{ $ort{ $next_2 } };
			$pos{ $next_2 }		=	"NN";
			$exp{ $next_3 }		=	"<NONE>";
			$pos{ $next_4 }		=	"NUM YEAR";
			
		}
	} # end exists
}
#**************************************************************#
#
# Example:	måndagen 3/1
#		den 3/1
#		måndagen den 3/1
#
#**************************************************************#
sub date_3 {

	if (exists ($ort{ $next_4 } )) {

		# Måndag 3/1
		if (
			# Context
			$type{ $curr }	ne	"DATE"
			&&
			$ort{ $curr }	=~	/^(?:$weekday|$weekday_abbr|the$)/io
			&&
			$pos{ $next_1 }	eq	"DEL"
			&&
			$ort{ $next_2 }	=~	/^$date_digit_format$/
			&&
			$ort{ $next_3 }	eq	"/"
			&&
			$ort{ $next_4 }	=~	/^$month_digit_format$/
			
		) {
			# Retag
			$type{ $curr }		=	"DATE";
			$type{ $next_1 }	=	"DATE";
			$type{ $next_2 }	=	"DATE";
			$type{ $next_3 }	=	"DATE";
			$type{ $next_4 }	=	"DATE";
			
			$pos{ $next_2 }		=	"NUM ORD";
			$pos{ $next_3 }		=	"SLASH";
			$exp{ $next_3 }		=	"i";
			$pos{ $next_4 }		=	"NUM ORD";
				
		}
	} # end exists

	if (exists ($ort{ $next_6 } )) {
		
		# Måndagen den 3/1
		if (
			# Context
			$ort{ $curr }	=~	/^(?:$weekday|$weekday_abbr|den)$/io	# måndagen
			&&
			$pos{ $next_1 }	eq	"DEL"	
			&&
			$ort{ $next_2 }	eq	"den"						# den
			&&
			$pos{ $next_3 }	eq	"DEL"
			&&
			$ort{ $next_4 }	=~	/^$date_digit_format$/				# 3
			&&
			$ort{ $next_5 }	eq	"/"						# /
			&&
			$ort{ $next_6 }	=~	/^$month_digit_format$/				# 1
			
		) {
			# Retag
			$type{ $curr }		=	"DATE";
			$type{ $next_1 }	=	"DATE";
			$type{ $next_2 }	=	"DATE";
			$type{ $next_3 }	=	"DATE";
			$type{ $next_4 }	=	"DATE";
			$type{ $next_5 }	=	"DATE";
			$type{ $next_6 }	=	"DATE";
			
			$pos{ $next_4 }		=	"NUM ORD";
			$pos{ $next_5 }		=	"SLASH";
			$exp{ $next_5 }		=	"i";
			$pos{ $next_6 }		=	"NUM ORD";
				
		}
	
	} # end exists}
}
#**************************************************************#
#
# Example:	2006-07-12
#
#**************************************************************#
sub date_4 {
	
	if (exists ($ort{ $next_4 } )) {

		if (
			# Context
			$type{ $curr }	ne	"DATE"
			&&
			$ort{ $curr }	=~	/$year_format/		# 2006
			&&
			$ort{ $next_1 }	eq	"-"			# -
			&&
			$ort{ $next_2 }	=~	/$month_digit_0/	# 07
			&&
			$ort{ $next_3 }	eq	"-"			# -
			&&
			$ort{ $next_4 }	=~	/$date_digit_format/	# 12
		) {
			# Retag
			$type{ $curr }		=	"DATE";
			$type{ $next_1 }	=	"DATE";
			$type{ $next_2 }	=	"DATE";
			$type{ $next_3 }	=	"DATE";
			$type{ $next_4 }	=	"DATE";
			
	
			$pos{ $curr }		=	"NUM YEAR";
			$pos{ $next_2 }		=	"NUM CARD";
			$pos{ $next_4 }		=	"NUM CARD";
			
			$exp{ $next_1 }		=	"<NONE>";
			$exp{ $next_3 }		=	"<NONE>";
			
		}
	} # end exists
}	
#**************************************************************#
#
# Example:	11/16/2003, 11-16-2003, 11.16.2003 or 11.16.03 
#
#**************************************************************#
sub date_5 {
	
	if (exists ($ort{ $next_4 } )) {

		if (
			# Context
			$type{ $curr }	ne	"DATE"
			&&
			$ort{ $curr }	=~	/$month_digit_0/	# 05
			&&
			$ort{ $next_1 }	=~	/^[\/\.\-]$/		# -
			&&
			$ort{ $next_2 }	=~	/$date_digit_format/	# 07
			&&
			$ort{ $next_3 }	eq	$ort{ $next_1 }		# -
			&&
			$ort{ $next_4 }	=~	/$year_digit_format/	# 1999
		) {
			# Retag
			$type{ $curr }		=	"DATE";
			$type{ $next_1 }	=	"DATE";
			$type{ $next_2 }	=	"DATE";
			$type{ $next_3 }	=	"DATE";
			$type{ $next_4 }	=	"DATE";
			
	
			$pos{ $curr }		=	"NUM YEAR";
			$pos{ $next_2 }		=	"NUM CARD";
			$pos{ $next_4 }		=	"NUM CARD";
			
			$exp{ $next_1 }		=	"<NONE>";
			$exp{ $next_3 }		=	"<NONE>";
			
		}
	} # end exists
}	

#**************************************************************#
#
# Example:	November 16, 2003 
#		Nov. 16, 2003 
#
#**************************************************************#
sub date_6 {
	
	if (exists ($ort{ $next_4 } )) {

		if (
			# Context
			$type{ $curr }	ne	"DATE"
			&&
			$ort{ $curr }	=~	/$month_letter_format/	# Jan.
			&&
			$pos{ $next_1 }	=~	"DEL"			# 
			&&
			$ort{ $next_2 }	=~	/$date_digit_format/	# 17
			&&
			$ort{ $next_3 }	=~	/^[ ,]$/		
			&&
			$ort{ $next_4 }	=~	/$year_format/		# 1999
		) {
			# Retag
			$type{ $curr }		=	"DATE";
			$type{ $next_1 }	=	"DATE";
			$type{ $next_2 }	=	"DATE";
			$type{ $next_3 }	=	"DATE";
			$type{ $next_4 }	=	"DATE";
			
	
			$pos{ $curr }		=	"NN";
			$pos{ $next_2 }		=	"NUM ORD";
			$pos{ $next_4 }		=	"NUM YEAR";

			# Expand month abbreviations			
			if ( exists ( $month_abbreviation{ $ort{ $curr } } ) ) {
				$exp{ $curr }		=	$month_abbreviation{ $ort{ $curr } };
			}

			$exp{ $next_1 }		=	"<NONE>";
			$exp{ $next_3 }		=	"<NONE>";
			
		}
	} # end exists
}	
#**************************************************************#
#
# Example:	21-22 mars 1983
#		7-8 febr.
#
#**************************************************************#
sub date_interval_1 {

	# 21-22 mars 1983
	if (exists ($ort{ $next_6 } )) {

		if (
		
			# Context
			$ort{ $curr }	=~	/^$date_digit_format$/		# 21
			&&
			$ort{ $next_1 }	eq	"-"				# -
			&&
			$ort{ $next_2 }	=~	/^$date_digit_format$/		# 22
			&&
			$pos{ $next_3 }	eq	"DEL"				#  
			&&
			$ort{ $next_4 }	=~	/^$month_letter_format$/	# mars
			&&
			$pos{ $next_5 }	eq	"DEL"				#  
			&&
			$ort{ $next_6 }	=~	/^$year_format$/		# 1983
	
	
			# Restrictions
			# The first date number must be lower than the second.
			&&
			$ort{ $curr }	<	$ort{ $next_2 }			# 21 < 22
			
		) {
		
			# Retag
			$type{ $curr }		=	"DATE";
			$type{ $next_1 }	=	"DATE";
			$type{ $next_2 }	=	"DATE";
			$type{ $next_3 }	=	"DATE";
			$type{ $next_4 }	=	"DATE";
			$type{ $next_5 }	=	"DATE";
			$type{ $next_6 }	=	"DATE";
		
			$pos{ $curr }		=	"NUM ORD";
			$pos{ $next_1 }		=	"DASH";
			$exp{ $next_1 }		=	"to";
			$pos{ $next_2 }		=	"NUM ORD";
			$pos{ $next_6 }		=	"NUM YEAR";	

			$exp{ $next_3 }		=	"of";

			# Expand month abbreviations			
			if ( exists ( $month_abbreviation{ $ort{ $curr } } ) ) {
				$exp{ $curr }		=	$month_abbreviation{ $ort{ $curr } };
			}
		
		}
	} # end exists
	
	
	# 7-9 febr.
	if (exists ($ort{ $next_4 } )) {

		if (
		
			# Context
			$ort{ $curr }	=~	/^$date_digit_format$/		# 21
			&&
			$ort{ $next_1 }	=~	/^-$/				# -
			&&
			$ort{ $next_2 }	=~	/^$date_digit_format$/		# 22
			&&
			$pos{ $next_3 }	=~	/^DEL$/				#  
			&&
			$ort{ $next_4 }	=~	/^$month_letter_format$/	# mars
	
	
			# Restrictions
			# The first date number must be lower than the second.
			&&
			$ort{ $curr }	<	$ort{ $next_2 }			# 21 < 22
			
		) {
		
			# Retag
			$type{ $curr }		=	"DATE";
			$type{ $next_1 }	=	"DATE";
			$type{ $next_2 }	=	"DATE";
			$type{ $next_3 }	=	"DATE";
			$type{ $next_4 }	=	"DATE";
		
			$pos{ $curr }		=	"NUM ORD";
			$pos{ $next_1 }		=	"DASH";
			$exp{ $next_1 }		=	"to";
			$pos{ $next_2 }		=	"NUM ORD";
			$exp{ $next_3 }		=	"of";
		
			# Expand month abbreviations			
			if ( exists ( $month_abbreviation{ $ort{ $curr } } ) ) {
				$exp{ $curr }		=	$month_abbreviation{ $ort{ $curr } };
			}
		}
	} # end exists
} # end sub
#**************************************************************#
#
# Example:	21-22/3-1983
#		
#**************************************************************#
sub date_interval_2 {

	if (exists ($ort{ $next_6 } )) {

		if (
		
			# Context
			$ort{$curr}	=~	/^$date_digit_format$/		# 21
			&&
			$ort{$next_1}	=~	/^-$/				# -
			&&
			$ort{$next_2}	=~	/^$date_digit_format$/		# 22
			&&
			$ort{$next_3}	=~	/^\/$/				# /
			&&
			$ort{$next_4}	=~	/^$month_digit_format$/		# 3
			&&
			$ort{$next_5}	=~	/^-$/				# -
			&&
			$ort{$next_6}	=~	/^$year_format$/		# 1983
	
			# Restrictions
			# The first date number must be lower than the second.
			&&
			$ort{$curr}	<	$ort{$next_2}			# 21 < 22
			
		) {
		
			# Retag
			$type{$curr}	=	"DATE";	
			$type{$next_1}	=	"DATE";	
			$type{$next_2}	=	"DATE";	
			$type{$next_3}	=	"DATE";	
			$type{$next_4}	=	"DATE";	
			$type{$next_5}	=	"DATE";	
			$type{$next_6}	=	"DATE";	
		
			$pos{$curr}	=	"NUM ORD";	
			$pos{$next_1}	=	"DASH";	
			$exp{$next_1}	=	"to";
			$pos{$next_2}	=	"NUM ORD";	
			$pos{$next_3}	=	"SLASH";	
			$exp{$next_3}	=	"i";
			$pos{$next_4}	=	"NUM ORD";	
			$pos{$next_5}	=	"DASH";	
			$exp{$next_5}	=	"<NONE>";	
			$pos{$next_6}	=	"NUM YEAR";	
		
		}
		
	} # end exists
}
#**************************************************************#
#
# Example:	monday-thursday		-->	monday to thursday
#		mond.-thursd.		-->	monday to thursday
# 		monday - thursday	-->	monday to thursday
#		mond. - thursd.		-->	monday to thursday
#
#**************************************************************#
sub rule_date_weekday {
	
	if (exists ($ort{ $next_2 } )) {

		# monday-thursday
		if (
		
			# Context
			$ort{ $curr }	=~	/^(?:$weekday|$weekday_abbr)$/io
			&&
			$ort{ $next_1 }	=~	/^-$/
			&&
			$ort{ $next_2 }	=~	/^(?:$weekday|$weekday_abbr)$/io
		) {
		
			# Retag
			$type{ $curr }		=	"DATE";
			$type{ $next_1 }	=	"DATE";
			$type{ $next_2 }	=	"DATE";
	
			$exp{ $next_1 }		=	"to";

			# Expand month abbreviations			
			if ( exists ( $month_abbreviation{ $ort{ $curr } } ) ) {
				$exp{ $curr }		=	$month_abbreviation{ $ort{ $curr } };
			}
			if ( exists ( $month_abbreviation{ $ort{ $next_2 } } ) ) {
				$exp{ $next_2 }		=	$month_abbreviation{ $ort{ $next_2 } };
			}
		}			
	} # end exists

	
	if (exists ($ort{ $next_4 } )) {
	
		# monday - thursday
		if (
		
			# Context
			$ort{ $curr }	=~	/^(?:$weekday|$weekday_abbr)$/io
			&&
			$pos{ $next_1 }	eq	"DEL"
			&&
			$ort{ $next_2 }	=~	/^-$/
			&&
			$pos{ $next_3 }	eq	"DEL"
			&&
			$ort{ $next_4 }	=~	/^(?:$weekday|$weekday_abbr)$/io
		) {
		
			# Retag
			$type{ $curr }	=	"DATE";
			$type{ $next_1 }	=	"DATE";
			$type{ $next_2 }	=	"DATE";
			$type{ $next_3 }	=	"DATE";
			$type{ $next_4 }	=	"DATE";
	
			$exp{ $next_2 }	=	"to";
			
			# Expand month abbreviations			
			if ( exists ( $month_abbreviation{ $ort{ $curr } } ) ) {
				$exp{ $curr }		=	$month_abbreviation{ $ort{ $curr } };
			}
			if ( exists ( $month_abbreviation{ $ort{ $next_4 } } ) ) {
				$exp{ $next_4 }		=	$month_abbreviation{ $ort{ $next_4 } };
			}
		}
	} # end exists
	
}
#**************************************************************#
# Doublecheck
# Example:	1 januari 1987
#		1 januari, 1987
#
#**************************************************************#
sub include_year {

#	print "$ort{ $prev_1 }\t$type{ $prev_1 }\t$ort{$curr}\t$type{$curr}\n\n";	
	
	if (exists ($ort{ $prev_2 } )) {

		# 1 januari 1987
		if (
			# Context
			$type{ $curr }		ne	"DATE"
			&&
			$type{ $prev_2 }	eq	"DATE"
			&&
			$pos{ $prev_1 }		eq	"DEL"
			&&
			$ort{ $curr }		=~	/^$year_format$/o
		) {
			# Retag
			$pos{ $curr }		=	"NUM YEAR";
			$type{ $curr }		=	"DATE";
			
		}
	
	}
		
	if (exists ($ort{ $prev_3 } )) {
	
		# 1 januari, 1987
		if (
			# Context
			$type{ $curr }		ne	"DATE"
			&&
			$type{ $prev_3 }	eq	"DATE"
			&&
			$pos{ $prev_2 }	eq	"DEL"
			&&
			$ort{ $prev_1 }		eq	","
			&&
			$ort{ $curr }		=~	/^$year_format$/o
		) {
			# Retag
			$type{ $curr }		=	"DATE";
			$type{ $prev_1 }	=	"DATE";
	
			$pos{ $next_1 }		=	"NUM YEAR";
			$type{ $next_1 }	=	"DATE";
			
		}
	} # end exists
}
#**************************************************************#
# Doublecheck
# Example:	1-2 januari
#		25-29/7
#
#**************************************************************#
sub include_interval {
	
	if (exists ($ort{ $next_2 } )) {

		if (
			# Context
			$type{ $curr }		ne	"DATE"
			&&
			$ort{ $curr }	=~	/^$date_digit_format$/o
			&&
			$ort{ $next_1 }	eq	"-"
			&&
			$type{ $next_2 }	eq	"DATE"
			&&
			
			# Current number must be lower than second number in interval.
			$ort{ $curr }	<	$ort{ $next_2 }
		) {
			# Retag
			$type{ $curr }	=	"DATE";
			$type{ $next_1 }	=	"DATE";
	
			$pos{ $curr }	=	"NUM ORD";
			$pos{ $next_1 }	=	"DASH";
			$exp{ $next_1 }	=	"to";
		}		
	} # end exists
}
#**************************************************************#
# Doublecheck
# Example:	the 1 January
#
#**************************************************************#
sub include_the {
	
	if (exists ($ort{ $next_2 } )) {

#		print "\n------------\ninclude_den\nCurr: $ort{$curr}\tNext_1: $type{$next_1}\tNext_2: $type{$next_2}\n\n";

		if (
			# Context
			$type{ $curr }		ne	"DATE"
			&&
			$ort{ $curr }		eq	"the"
			&&
			$pos{ $next_1 }		eq	"DEL"
			&&
			$type{ $next_2 }	eq	"DATE"
			
		) {
			# Retag
			$type{ $curr }		=	"DATE";
			$type{ $next_1 }	=	"DATE";

		}			
	} # end exists
}
#**************************************************************#
# Doublecheck
# Example:	fredag 4 januari
#
#**************************************************************#
sub include_weekday {
	
	
	
	if (exists ($ort{ $next_2 } )) {

#		print "
#		-----------------
#		include_weekday
#		Curr:	$ort{$curr}\n
#		Next1:	$pos{$next_1}
#		Next2:	$type{$next_2}\n\n";

		if (
			# Context
			$type{ $curr }		ne	"DATE"
			&&
			$ort{ $curr }		=~	/(?:$weekday|$weekday_abbr)/io
			&&
			$pos{ $next_1 }		eq	"DEL"
			&&
			$type{ $next_2 }	eq	"DATE"
			
		) {
			
			# Retag
			$type{ $curr }		=	"DATE";
			$type{ $next_1 }	=	"DATE";
			
			# Expand month abbreviations			
			if ( exists ( $month_abbreviation{ $ort{ $curr } } ) ) {
				$exp{ $curr }		=	$month_abbreviation{ $ort{ $curr } };
			}
		}
	} # end exists
}
#**************************************************************#
#
# Example:	1 jan. - 4 feb.
#
#**************************************************************#
sub include_dates {
	
	if (exists ($ort{ $prev_1 } ) && exists ($ort{ $next_1 } )) {
		
		# 31/7-3/8
		if (
			# Context
			$type{ $curr }		ne	"DATE"
			&&
			$type{ $prev_1 }	eq	"DATE"
			&&
			$ort{ $curr }	eq	"-"
			&&
			$type{ $next_1 }	eq	"DATE"
			
		) {
			# Retag
			$type{ $curr }		=	"DATE";
			$exp{ $curr }		=	"to";
						
		}
	}
			
	if (exists ($ort{ $prev_2 } ) && exists ($ort{ $next_2 } )) {

		# 31/7 - 3/8
		if (
			# Context
			$type{ $curr }		ne	"DATE"
			&&
			$type{ $prev_2 }	eq	"DATE"
			&&
			$pos{ $prev_1 }	eq	"DEL"
			&&
			$ort{ $curr }	eq	"-"
			&&
			$pos{ $next_1 }	eq	"DEL"
			&&
			$type{ $next_2 }	eq	"DATE"
			
		) {
			# Retag
			$type{ $prev_1 }	=	"DATE";
			$type{ $curr }		=	"DATE";
			$type{ $next_1 }	=	"DATE";
			
			$exp{ $curr }		=	"to";
						
		}
	} # end exists
}
#**************************************************************#

1;

