#!/usr/bin/perl -w

#**************************************************************#
# stress rules for compounds etc.
#
#**************************************************************#


#**************************************************************#
# first_part_stress
#
# Accent II on first part of compound.
#**************************************************************#
sub first_part_stress {
	
	my $trans	=	shift;
	
	# Remove secondary stress
	$trans		=~	s/\`//g;
	
	# Change accent I to accent II
	$trans		=~	s/\'/\"/;
	
	return $trans;
	
}
#**************************************************************#
# second_part_stress
#
# Seconday stress on second part of compound.
#**************************************************************#
sub second_part_stress {
	
	my $trans	=	shift;
	
	# Secondary stress exists - remove primary stress
	if ( $trans	=~	/\`/ ) {
		$trans	=~	s/[\"\']//;
		
	# Else change primary stress to seconday stress
	} else {
		$trans	=~	s/[\"\']/\`/;
	}
	
	return $trans;	
	
}
#**************************************************************#
# second_part_acr_stress
#
# Seconday stress on second part of compound (acronym).
#**************************************************************#
sub second_part_acr_stress {
	
	my $trans	=	shift;
	
	# Else change primary stress to seconday stress
	$trans	=~	s/\`//;
	$trans	=~	s/[\"\']/\`/;
	
	return $trans;	
	
}
#**************************************************************#
1;
