#!/usr/bin/perl -w


#**************************************************************#
# run_rules.pl
#
# 
#**************************************************************#
sub run_rules {

	my $sent = shift;


	&run_initials_subs();

	if ( $sent =~ /\d/ ) {
		&run_date_subs();
		&run_time_subs();
	}
	
	if ( $sent =~ /\@/ ) {
		&run_email_subs();
	}
	
	if ( $sent =~ /(?:www|http)/ ) {
		&run_url_subs();
	}
	
#	&print_all_output();
	&run_filename_subs();
	

	# Moved from xxx----xxx
	# Do the same for Swedish???
	&run_roman_num_subs();
	
#	&print_all_output();

	&run_acronym_subs();
#	&run_unit_subs();

	
	if ( $sent =~ /\d/ ) {
		&run_decimal_num_subs();
		&run_mixed_num_subs();
	#	&run_phone_num_subs();
		&run_ordinals_num_subs();
		
		&run_interval_num_subs();
		
		&run_year_num_subs();
		
		&run_currency_subs();
	}


		
	&run_expansion_subs();
	&run_expansion_subs_2();

	
}
#************************************************************#
# run_acronym_subs
#************************************************************#
sub run_acronym_subs {
	
	# List length
	my @orthography_list	=	sort (keys( %orthography ) );
	my $list_length		=	$#orthography_list;

	%ort = %orthography;


	my $counter = 0;
	foreach my $curr (@orthography_list) {

		
		($index_list) = &assign_index($curr,$counter,$list_length);

		&acronym_subs($index_list);

		$counter++;

	} # end foreach @orthography_list

	%orthography = %ort;
}#************************************************************#
# run_currency_subs
#************************************************************#
sub run_currency_subs {
	
	# List length
	my @orthography_list	=	sort (keys( %orthography ) );
	my $list_length		=	$#orthography_list;

	%ort = %orthography;


	my $counter = 0;
	foreach my $curr (@orthography_list) {

		
		($index_list) = &assign_index($curr,$counter,$list_length);

		&currency_subs($index_list);

		$counter++;

	} # end foreach @orthography_list

	%orthography = %ort;
}
#************************************************************#
# run_expansion_subs
#************************************************************#
sub run_expansion_subs {
	
	# List length
	@orthography_list	=	sort (keys( %orthography ) );
	my $list_length		=	$#orthography_list;

	%ort = %orthography;


	my $counter = 0;
	foreach my $curr (@orthography_list) {

		
		($index_list) = &assign_index($curr,$counter,$list_length);

		&expansion_subs($index_list);

		$counter++;

	} # end foreach @orthography_list

	%orthography = %ort;
}
#************************************************************#
# run_expansion_subs_2
#************************************************************#
sub run_expansion_subs_2 {
	
	# List length
	my @orthography_list	=	sort (keys( %orthography ) );
	my $list_length		=	$#orthography_list;

	%ort = %orthography;


	my $counter = 0;
	foreach my $curr (@orthography_list) {

		
		($index_list) = &assign_index($curr,$counter,$list_length);

		&expansion_subs_2($index_list);

		$counter++;

	} # end foreach @orthography_list

	%orthography = %ort;
}
#************************************************************#
# run_unit_subs
#************************************************************#
sub run_unit_subs {
	
	# List length
	my @orthography_list	=	sort (keys( %orthography ) );
	my $list_length		=	$#orthography_list;

	%ort = %orthography;


	my $counter = 0;
	foreach my $curr (@orthography_list) {

		
		($index_list) = &assign_index($curr,$counter,$list_length);

		&unit_subs($index_list);

		$counter++;

	} # end foreach @orthography_list

	%orthography = %ort;
}
#************************************************************#
# run_initials_subs
#
# Runs twice to catch spreading name initials.
#************************************************************#
sub run_initials_subs {
	
	# List length
	my @orthography_list	=	sort (keys( %orthography ) );
	my $list_length		=	$#orthography_list;

	%ort = %orthography;

	my $counter = 0;
	foreach my $curr (@orthography_list) {

		($index_list) = &assign_index($curr,$counter,$list_length);


		# Date subs
		&initials_subs($index_list);

		$counter++;

	} # end foreach @orthography_list

			
	$counter = 0;
	foreach my $curr (@orthography_list) {

		($index_list) = &assign_index($curr,$counter,$list_length);

		# Date subs
		&initials_subs($index_list);

		$counter++;

	} # end foreach @orthography_list

	%orthography = %ort;
}
#************************************************************#
# run_interval_num_subs
#************************************************************#
sub run_interval_num_subs {
	
	# List length
	my @orthography_list	=	sort (keys( %orthography ) );
	my $list_length		=	$#orthography_list;

	%ort = %orthography;

	my $counter = 0;
	foreach my $curr (@orthography_list) {

		($index_list) = &assign_index($curr,$counter,$list_length);

		# Date subs
		&interval_num_subs($index_list);

		$counter++;

	} # end foreach @orthography_list

	%orthography = %ort;
}
#************************************************************#
# run_ordinals_num_subs
#************************************************************#
sub run_ordinals_num_subs {
	
	# List length
	my @orthography_list	=	sort (keys( %orthography ) );
	my $list_length		=	$#orthography_list;

	%ort = %orthography;

	my $counter = 0;
	foreach my $curr (@orthography_list) {

		($index_list) = &assign_index($curr,$counter,$list_length);

		# Date subs
		&ordinals_num_subs($index_list);

		$counter++;

	} # end foreach @orthography_list

	%orthography = %ort;
}
#************************************************************#
# run_year_num_subs
#************************************************************#
sub run_year_num_subs {
	
	# List length
	my @orthography_list	=	sort (keys( %orthography ) );
	my $list_length		=	$#orthography_list;

	%ort = %orthography;

	my $counter = 0;
	foreach my $curr (@orthography_list) {

		($index_list) = &assign_index($curr,$counter,$list_length);

		# Date subs
		&year_num_subs($index_list);

		$counter++;

	} # end foreach @orthography_list

	%orthography = %ort;
}
#************************************************************#
# run_roman_num_subs
#************************************************************#
sub run_roman_num_subs {
	
	# List length
	my @orthography_list	=	sort (keys( %orthography ) );
	my $list_length		=	$#orthography_list;

	%ort = %orthography;

	my $counter = 0;
	foreach my $curr (@orthography_list) {

		($index_list) = &assign_index($curr,$counter,$list_length);

		# Date subs
		&roman_num_subs($index_list);

		$counter++;

	} # end foreach @orthography_list

	%orthography = %ort;
	
	#-------------------------------#

	# List length
	@orthography_list	=	sort (keys( %orthography ) );
	$list_length		=	$#orthography_list;

	%ort = %orthography;
	$counter = 0;
	foreach my $curr (@orthography_list) {

		($index_list) = &assign_index($curr,$counter,$list_length);

		# Date subs
		&roman_num_merge_subs($index_list);

		$counter++;

	} # end foreach @orthography_list
	
	%orthography = %ort;

} # end sub
#************************************************************#
# run_mixed_num_subs
#************************************************************#
sub run_mixed_num_subs {
	
	# List length
	my @orthography_list	=	sort (keys( %orthography ) );
	my $list_length		=	$#orthography_list;

	%ort = %orthography;

	# Normal mixed_num subs
	my $counter = 0;
	foreach my $curr (@orthography_list) {

		($index_list) = &assign_index($curr,$counter,$list_length);

		# Date subs
		&mixed_num_subs($index_list);

		$counter++;

	} # end foreach @orthography_list

	# Concatenative mixed_num_subs
	$counter = 0;
	foreach my $curr (@orthography_list) {

		($index_list) = &assign_index($curr,$counter,$list_length);

		# Date subs
		&mixed_num_concat_subs($index_list);

		$counter++;

	} # end foreach @orthography_list
	%orthography = %ort;

} # end sub
#************************************************************#
# run_phone_num_subs
#************************************************************#
sub run_phone_num_subs {
	
	# List length
	my @orthography_list	=	sort (keys( %orthography ) );
	my $list_length		=	$#orthography_list;

	%ort = %orthography;

	my $counter = 0;
	foreach my $curr (@orthography_list) {

		($index_list) = &assign_index($curr,$counter,$list_length);

		# Date subs
		&phone_num_subs($index_list);

		$counter++;

	} # end foreach @orthography_list

	%orthography = %ort;

} # end sub
#************************************************************#
# run_decimal_num_subs
#************************************************************#
sub run_decimal_num_subs {
	
	# List length
	my @orthography_list	=	sort (keys( %orthography ) );
	my $list_length		=	$#orthography_list;

	%ort = %orthography;

	my $counter = 0;
	foreach my $curr (@orthography_list) {

		($index_list) = &assign_index($curr,$counter,$list_length);

		# Date subs
		&decimal_num_subs($index_list);

		$counter++;

	} # end foreach @orthography_list

	%orthography = %ort;

} # end sub
#************************************************************#
# run_merge_num_subs
#************************************************************#
sub run_merge_num_subs {
	
	# List length
	my @orthography_list	=	sort (keys( %orthography ) );
	my $list_length		=	$#orthography_list;

	%ort = %orthography;

	my $counter = 0;
	foreach my $curr (@orthography_list) {

		# Redo list length since fields might have been merged.
		# List length
		my @orthography_list	=	sort (keys( %ort ) );
		my $list_length		=	$#orthography_list;

#		print "OO @orthography_list\n$list_length\n\n";

		($index_list) = &assign_index($curr,$counter,$list_length);

		# Merge numerals subs
		&merge_num_subs($index_list);

		$counter++;

	} # end foreach @orthography_list
	
	%orthography = %ort;
	
} # end sub
#************************************************************#
# run_email_subs
#************************************************************#
sub run_email_subs {
	
	# List length
	my @orthography_list	=	sort (keys( %orthography ) );
	my $list_length		=	$#orthography_list;

	%ort = %orthography;

	my $counter = 0;
	foreach my $curr (@orthography_list) {

		($index_list) = &assign_index($curr,$counter,$list_length);

		# Date subs
		&email_subs($index_list);

		$counter++;

	} # end foreach @orthography_list

	%orthography = %ort;

} # end sub
#************************************************************#
# run_filename_subs
#************************************************************#
sub run_filename_subs {
	
	# List length
	my @orthography_list	=	sort (keys( %orthography ) );
	my $list_length		=	$#orthography_list;

	%ort = %orthography;

	my $counter = 0;
	foreach my $curr (@orthography_list) {

		($index_list) = &assign_index($curr,$counter,$list_length);

		# Date subs
		&filename_subs($index_list);

		$counter++;

	} # end foreach @orthography_list

	%orthography = %ort;

} # end sub
#************************************************************#
# run_url_subs
#************************************************************#
sub run_url_subs {
	
	# List length
	my @orthography_list	=	sort (keys( %orthography ) );
	my $list_length		=	$#orthography_list;

	%ort = %orthography;

	my $counter = 0;
	foreach my $curr (@orthography_list) {

		($index_list) = &assign_index($curr,$counter,$list_length);

		# Date subs
		&url_subs($index_list);

		$counter++;

	} # end foreach @orthography_list

	%orthography = %ort;

} # end sub
#************************************************************#
# run_time_subs
#************************************************************#
sub run_time_subs {
	
	# List length
	my @orthography_list	=	sort (keys( %orthography ) );
	my $list_length		=	$#orthography_list;

	%ort = %orthography;

	my $counter = 0;
	foreach my $curr (@orthography_list) {

		($index_list) = &assign_index($curr,$counter,$list_length);

		# Date subs
		&time_subs($index_list);

		$counter++;

	} # end foreach @orthography_list

	%orthography = %ort;

} # end sub
#************************************************************#
# run_date_subs
#************************************************************#
sub run_date_subs {
	
	# List length
	my @orthography_list	=	sort (keys( %orthography ) );
	my $list_length		=	$#orthography_list;

#	foreach $k (sort(keys(%orthography))) { print "$k\t$orthography{$k}\n\n", }
	
#	print "LIST_LENGTH: $list_length\n@orthography_list\n\n";

	%ort = %orthography;

	my $counter = 0;
	foreach my $curr (@orthography_list) {

		($index_list) = &assign_index($curr,$counter,$list_length);

		# Date subs
		&date_subs($index_list);
#		&include_subs($index_list);

		$counter++;

	} # end foreach @orthography_list

	$counter = 0;
	foreach my $curr (@orthography_list) {

		($index_list) = &assign_index($curr,$counter,$list_length);

		# Date subs
#		&date_subs($index_list);
		&include_subs($index_list);

		$counter++;

	} # end foreach @orthography_list

	%orthography = %ort;
	
}
#************************************************************#
#
# Assigns indexes to variables $prev_1, $curr, $next_1 a.s.o.
# Upto 6 fields from current location.
#
#************************************************************#
sub assign_index {
	
	my ($curr,$counter,$list_length) = @_;
	
	my ($prev_1,$prev_2,$prev_3,$prev_4,$prev_5,$prev6,$prev_7,$prev_8,$prev_9,$prev_10,$next_1,$next_2,$next_3,$next_4,$next_5,$next_6,$next_7,$next_8,$next_9,$next_10);
	
	my @orthography_list	=	sort (keys( %orthography ) );

	# Previous fields
	if ( $counter > 0 ) {	
		$prev_1	= $orthography_list[$counter-1];
	} else {
		$prev_1 = "<NONE>";
	}
		
	if ( $counter > 1 ) {	
		$prev_2 = $orthography_list[$counter-2];
	} else {
		$prev_2 = "<NONE>";
	}

	if ( $counter > 2 ) {	
		$prev_3 = $orthography_list[$counter-3];
	} else {
		$prev_3 = "<NONE>";
	}

	if ( $counter > 3 ) {	
		$prev_4 = $orthography_list[$counter-4];
	} else {
		$prev_4 = "<NONE>";
	}

	if ( $counter > 4 ) {	
		$prev_5 = $orthography_list[$counter-5];
	} else {
		$prev_5 = "<NONE>";
	}

	if ( $counter > 5 ) {	
		$prev_6 = $orthography_list[$counter-6];
	} else {
		$prev_6 = "<NONE>";
	}

	if ( $counter > 6 ) {	
		$prev_7 = $orthography_list[$counter-7];
	} else {
		$prev_7 = "<NONE>";
	}

	if ( $counter > 7 ) {	
		$prev_8 = $orthography_list[$counter-8];
	} else {
		$prev_8 = "<NONE>";
	}

	if ( $counter > 8 ) {	
		$prev_9 = $orthography_list[$counter-9];
	} else {
		$prev_9 = "<NONE>";
	}

	if ( $counter > 9 ) {	
		$prev_10 = $orthography_list[$counter-10];
	} else {
		$prev_10 = "<NONE>";
	}


	
	# Following fields
	if ( $counter < $list_length ) {	
		$next_1	= $orthography_list[$counter+1];
	} else {
		$next_1 = "<NONE>";
	}
		
	if ( $counter + 1 < $list_length ) {	
		$next_2 = $orthography_list[$counter+2];
	} else {
		$next_2 = "<NONE>";
	}

	if ( $counter + 2 < $list_length ) {	
		$next_3 = $orthography_list[$counter+3];
	} else {
		$next_3 = "<NONE>";
	}

	if ( $counter + 3 < $list_length ) {	
		$next_4 = $orthography_list[$counter+4];
	} else {
		$next_4 = "<NONE>";
	}

	if ( $counter + 4 < $list_length ) {	
		$next_5 = $orthography_list[$counter+5];
	} else {
		$next_5 = "<NONE>";
	}
	
	if ( $counter + 5 < $list_length ) {	
		$next_6 = $orthography_list[$counter+6];
	} else {
		$next_6 = "<NONE>";
	}

	if ( $counter + 6 < $list_length ) {	
		$next_7 = $orthography_list[$counter+7];
	} else {
		$next_7 = "<NONE>";
	}

	if ( $counter + 7 < $list_length ) {	
		$next_8 = $orthography_list[$counter+8];
	} else {
		$next_8 = "<NONE>";
	}

	if ( $counter + 8 < $list_length ) {	
		$next_9 = $orthography_list[$counter+9];
	} else {
		$next_9 = "<NONE>";
	}

	if ( $counter + 9 < $list_length ) {	
		$next_10 = $orthography_list[$counter+10];
	} else {
		$next_10 = "<NONE>";
	}


#	print "CURR: $curr\t\tCOUNTER: $counter\nP1: $prev_10\t\tN1: $next_1\t\tN6: $next_10\n\n";exit;


	my $index_list = "$prev_10|$prev_9|$prev_8|$prev_7|$prev_6|$prev_5|$prev_4|$prev_3|$prev_2|$prev_1|$curr|$next_1|$next_2|$next_3|$next_4|$next_5|$next_6|$next_7|$next_8|$next_9|$next_10";
	
	return $index_list;

} # end sub
#************************************************************#
# Renumber fields
#************************************************************#
sub renumber_fields {
	
	my %temp_ort	=	();
	my $counter	=	0;

	foreach my $k (sort (keys (%ort) )) {
		my $ort	=	$ort{ $k };
#		print "REN: $counter\t$k\n";
		$k	=	$counter;
		$k	=	&format_pcounter( $k );
		
		$temp_ort{ $k }	=	$ort;
		
#		print "REN: $counter\t$k\n\n";
		$counter++;

	}
	
	%ort		=	%temp_ort;
}
#************************************************************#
# Insert field
#************************************************************#
sub insert_field {
	
	my ($location,$string1,$string2)	=	@_;
	
	my %insert_ort	=	();
	my $counter	=	0;
	
	foreach my $k (sort (keys ( %ort ) )) {
		my $ort	=	$ort{ $k };

		$k	.=	"0";
		
		$insert_ort{ $k }	= $ort;
		
			
	}
	
#	print "LOX: $location\n";

#	$insert_ort{ $

	
}
1;
