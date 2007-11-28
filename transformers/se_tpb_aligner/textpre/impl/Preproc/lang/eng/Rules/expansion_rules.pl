#!/usr/bin/perl -w

#**************************************************************#
sub expansion_subs {
	
	my $index_list = shift;
	($prev_10,$prev_9,$prev_8,$prev_7,$prev_6,$prev_5,$prev_4,$prev_3,$prev_2,$prev_1,$curr,$next_1,$next_2,$next_3,$next_4,$next_5,$next_6,$next_7,$next_8,$next_9,$next_10) = split/\|/,$index_list;

	my $types = join" ",values(%type);
	my $poses = join" ",values(%pos);

	if ( $types =~ /NUM/ || $poses =~ /NUM/ ) {

		# Numeral expansions.
		&num_card_expansion();
		&num_ord_expansion();
		&num_year_expansion();
	}
	
	
	if ( $types =~ /ABBR/ || $poses =~ /ABBR/ ) {
		# Abbreviation expansions.
		&abbreviation_expansion();
	}

	# Unit expansions
	&unit_expansion();
	
	
	# Character expansions.
	&character_expansion();

}
#**************************************************************#
sub expansion_subs_2 {

	my $index_list = shift;
	($prev_10,$prev_9,$prev_8,$prev_7,$prev_6,$prev_5,$prev_4,$prev_3,$prev_2,$prev_1,$curr,$next_1,$next_2,$next_3,$next_4,$next_5,$next_6,$next_7,$next_8,$next_9,$next_10) = split/\|/,$index_list;

	# Currency expansions
	&currency_expansion();
}
#**************************************************************#
1;
