#!/usr/bin/perl -w

#***********************************************************#
#	Counting number of components in input strint.
#
#	CE 070307
#***********************************************************#
	# 1. Split on blanks only.
	# 2. Lexicon lookup.
	# 3. For unknown words: split on delimiters and digits.
#***********************************************************#
# count_components_blanks
#
# First lexicon lookup on string splitted only on blanks.
#***********************************************************#
sub count_components_blanks {

	my $orthography = shift;	
	
	
	$orthography = &clean_blanks($orthography);
			
	
	# 1. Split on blanks only.
	$orthography =~ s/( +)/<SPLITTER>$1<SPLITTER>/g;

	$orthography =~ s/(\d)(:-)/$1<SPLITTER>$2<SPLITTER>/g;
	

	$orthography =~ s/(<SPLITTER>)+/<SPLITTER>/g;
	$orthography =~ s/^<SPLITTER>//;
	$orthography =~ s/<SPLITTER>$//;

#	print "$orthography\n";
	
	@orthography = split/<SPLITTER>/,$orthography;
	

	my $number_of_components = $#orthography;

	return $number_of_components;
}
#***********************************************************#
# count_components
#
# For unknown words, split on delimiters and digits.
#***********************************************************#
sub count_components {
	
	# 2. Lexicon lookup has been done.
	
	# 3. For unknown words: split at delimiters and digits.
	
	my $all_delimiters = quotemeta($all_delimiters);

	my @o_list	=	sort(keys(%orthography));
	my $splitted	=	"";
	foreach my $o ( @o_list ) {
#		print "NU: $o\n";
		
		if ($pos{ $o }	eq	"UNK" ) {

#			print "UNK: $pos{$o}\t$orthography{ $o }\n";
			
			# Split on delimiters
			$orthography{ $o }	=~	 s/([$all_delimiters]|[\@_\%\/\\])/<SPLITTER>$1<SPLITTER>/g;

			# Split on digits
			$orthography{ $o }	=~	s/(\d+)/<SPLITTER>$1<SPLITTER>/g;
	
			# Merge full stops with abbreviations.
			# Example:	"tisd"	"."	-->	"tisd."
#			$orthography{ $o }	=~	s/(<SPLITTER>|^)($abbreviation_list)<SPLITTER>\.<SPLITTER>/$1$2\.<SPLITTER>/iog;

			# Split intervals with roman numbers.
			$orthography{ $o } =~ s/([MDCLXVI]+)-([MDCLXVI]+)/$1<SPLITTER>-<SPLITTER>$2/g;
			$orthography{ $o } =~ s/([mdclxvi]+)-([mdclxvi]+)/$1<SPLITTER>-<SPLITTER>$2/g;

			# Do not split ":-"
			$orthography{ $o } =~ s/<SPLITTER>:<SPLITTER>(-(?:<SPLITTER>|$))/<SPLITTER>:$1/g;


			# Stout&Hedges
			$orthography{ $o } =~ s/([a-zедц])\&([a-zедц])/$1<SPLITTER>\&<SPLITTER>$2/ig;
#			print "OO $orthography{ $o }\n";

			# Do not split "'s" etc. (I've, you'd, development's...)
			$orthography{ $o } =~ s/([a-z])<SPLITTER>\'<SPLITTER>([a-z])/$1\'$2/ig;

#			print "OOO $orthography{ $o }\n";

			# Do not split "&...;" (&gt; etc.)
			$orthography{ $o } =~ s/(\&[a-z]+)<SPLITTER>\;<SPLITTER>/$1\;<SPLITTER>/g;

#			print "OOO $orthography{ $o }\n";


			$orthography{ $o } =~ s/((?:<SPLITTER>|^|-)[a-zедц]+)<SPLITTER>:<SPLITTER>($acronym_endings(?:<SPLITTER>|$))/$1:$2/ig;



			# Remove redundant <SPLITTER>:s.
			$orthography{ $o }	=~	s/(<SPLITTER>)+/<SPLITTER>/g;
			$orthography{ $o }	=~	s/^<SPLITTER>//;
			$orthography{ $o }	=~	s/<SPLITTER>$//;

			$orthography{ $o } =~ s/(\d+)<SPLITTER>,<SPLITTER>(\d\d\d)\b/$1,$2/g;

		} # end if /UNK/

#		print "READY: $orthography{ $o }\n\n";
		$splitted	.=	"$orthography{ $o }<SPLITTER>";
	

#		print "xx $orthography{ $o }\n";
	
	} # end foreach
	
#	print "OO $splitted\n\n";
	
	@orthography	=	split/<SPLITTER>/,$splitted;
	
			
	my $number_of_components = $#orthography;

	return $number_of_components;
}
#***********************************************************#

1;
